#include <iostream>
#include <thread>
#include <mqtt/async_client.h>
#include <nlohmann/json.hpp>
#include <random>
#include <chrono>
#include <iomanip>
#include <cpprest/http_client.h>
#include <fstream>
#include <unordered_map>

using json = nlohmann::json;

struct Config {
    std::string mqtt_address;
    std::string mqtt_client_id;
    std::string mqtt_topic;
    int mqtt_qos;
    std::string sensor_api_url;
    std::string car_api_url;
    std::string car_type_api_url;
    int publish_interval;
};


Config loadConfig(const std::string& filename) {
    std::ifstream configFile(filename);
    if (!configFile) {
        throw std::runtime_error("Failed to open config file");
    }

    json configJson;
    configFile >> configJson;

    Config config;
    config.mqtt_address = configJson["mqtt_broker"]["address"];
    config.mqtt_client_id = configJson["mqtt_broker"]["client_id"];
    config.mqtt_topic = configJson["mqtt_broker"]["topic"];
    config.mqtt_qos = configJson["mqtt_broker"]["qos"];
    config.sensor_api_url = configJson["sensor_api"]["url"];
    config.car_api_url = configJson["car_api"]["url"];
    config.car_type_api_url = configJson["car_type_api"]["url"];
    config.publish_interval = configJson["publish_interval"];

    return config;
}


std::string getCurrentTimeISO8601() {
    auto now = std::chrono::system_clock::now();
    auto time_t_now = std::chrono::system_clock::to_time_t(now);
    auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch()) % 1000;

    std::ostringstream oss;
    oss << std::put_time(std::gmtime(&time_t_now), "%Y-%m-%dT%H:%M:%S");
    oss << "." << std::setfill('0') << std::setw(3) << ms.count() << "Z";
    return oss.str();
}



double generateRandomValue(double min, double max) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<> dist(min, max);
    return dist(gen);
}


double generateSensorValue(const std::string& sensorType) {
    if (sensorType == "Tire Pressure") {
        return generateRandomValue(1.0, 10.0);
    } else if (sensorType == "Fuel Level") {
        return generateRandomValue(0.0, 100);
    } else if (sensorType == "Temperature") {
        return generateRandomValue(40.0, 120.0);
    }
    return 0.0;
}


std::vector<json> fetchSensors(const std::string& apiUrl) {
    web::http::client::http_client client(utility::conversions::to_string_t(apiUrl));

    web::http::http_response response = client.request(web::http::methods::GET).get();

    if (response.status_code() != web::http::status_codes::OK) {
        throw std::runtime_error("Failed to fetch sensors, HTTP Status: " + std::to_string(response.status_code()));
    }

    auto responseBody = response.extract_string().get();
    return json::parse(responseBody);
}


json fetchCarFromApi(const std::string& apiUrl, int carId) {
    std::string carUrl = apiUrl + "/" + std::to_string(carId);

    web::http::client::http_client client(utility::conversions::to_string_t(carUrl));

    web::http::http_response response = client.request(web::http::methods::GET).get();

    if (response.status_code() != web::http::status_codes::OK) {
        throw std::runtime_error("Failed to fetch car details for carId " + std::to_string(carId) +
                                 ", HTTP Status: " + std::to_string(response.status_code()));
    }

    auto responseBody = response.extract_string().get();
    return json::parse(responseBody);
}


json fetchCarTypeById(const std::string& apiUrl, int carTypeId) {
    std::string carTypeUrl = apiUrl + "/" + std::to_string(carTypeId);

    web::http::client::http_client client(utility::conversions::to_string_t(carTypeUrl));

    web::http::http_response response = client.request(web::http::methods::GET).get();

    if (response.status_code() != web::http::status_codes::OK) {
        throw std::runtime_error("Failed to fetch car type for carTypeId " + std::to_string(carTypeId) +
                                 ", HTTP Status: " + std::to_string(response.status_code()));
    }

    auto responseBody = response.extract_string().get();
    return json::parse(responseBody);
}


double calculateTechnicalCoefficientWithThresholds(
    const std::unordered_map<std::string, double>& sensorValues,
    const json& carType)
{
    double weightedSum = 0.0;
    double totalWeight = 0.0;

    double minEngineTemp = carType["minEngineTemp"];
    double maxEngineTemp = carType["maxEngineTemp"];
    double minFuelLevel = carType["minFuelLevel"];
    double maxFuelLevel = carType["maxFuelLevel"];
    double minTirePressure = carType["minTirePressure"];
    double maxTirePressure = carType["maxTirePressure"];

    for (const auto& sensor : sensorValues) {
        const auto& sensorType = sensor.first;
        double value = sensor.second;

        double weight = 1.0;
        if (sensorType == "Tire Pressure") weight = 3.0;
        else if (sensorType == "Temperature") weight = 2.5;
        else if (sensorType == "Fuel Level") weight = 1.5;

        double deviation = 0.0;

        if (sensorType == "Tire Pressure") {
            if (value < minTirePressure) deviation = minTirePressure - value;
            else if (value > maxTirePressure) deviation = value - maxTirePressure;
        } else if (sensorType == "Temperature") {
            if (value < minEngineTemp) deviation = minEngineTemp - value;
            else if (value > maxEngineTemp) deviation = value - maxEngineTemp;
        } else if (sensorType == "Fuel Level") {
            if (value < minFuelLevel) deviation = minFuelLevel - value;
            else if (value > maxFuelLevel) deviation = value - maxFuelLevel;
        }

        double normalizedScore = std::max(1.0, 10.0 - deviation);

        weightedSum += normalizedScore * weight;
        totalWeight += weight;
    }

    if (totalWeight == 0.0) return 5.0;
    return weightedSum / totalWeight;
}


int main() {
    try {
        Config config = loadConfig("C:/Users/user/CLionProjects/untitled2/config.json");

        mqtt::async_client client(config.mqtt_address, config.mqtt_client_id);

        mqtt::connect_options connOpts;
        std::cout << "Connecting to MQTT broker: " << config.mqtt_address << std::endl;
        client.connect(connOpts)->wait();
        std::cout << "Connected to MQTT broker." << std::endl;

        while (true) {
            std::vector<json> sensors = fetchSensors(config.sensor_api_url);
            std::cout << "Fetched " << sensors.size() << " sensors." << std::endl;

            std::unordered_map<int, std::unordered_map<std::string, double>> sensorValues;

            for (const auto& sensor : sensors) {
                std::string sensorType = sensor["sensorType"];
                double value = generateSensorValue(sensorType);

                json payload = {
                    {"sensorId", sensor["id"]},
                    {"readingTime", getCurrentTimeISO8601()},
                    {"parameterType", sensorType},
                    {"value", value}
                };

                std::cout << "Publishing data: " << payload.dump() << std::endl;
                client.publish(config.mqtt_topic, payload.dump(), config.mqtt_qos, false)->wait();

                int carId = sensor["carId"];
                sensorValues[carId][sensorType] = value;
            }


            for (const auto& carSensorValues : sensorValues) {
                int carId = carSensorValues.first;
                const auto& sensorValuesForCar = carSensorValues.second;

                try {
                    json carDetails = fetchCarFromApi(config.car_api_url, carId);
                    std::cout << "Car ID: " << carId << ", Car Details: " << carDetails.dump() << std::endl;

                    int carTypeId = carDetails["carTypeId"];
                    json carType = fetchCarTypeById(config.car_type_api_url, carTypeId);
                    std::cout << "Car ID: " << carId << ", Car Type: " << carType.dump() << std::endl;

                    double technicalCoefficient = calculateTechnicalCoefficientWithThresholds(sensorValuesForCar, carType);
                    std::cout << "Technical coefficient for car ID " << carId << ": " << technicalCoefficient << std::endl;

                    json coeffPayload = {
                        {"carId", carId},
                        {"readingTime", getCurrentTimeISO8601()},
                        {"value", technicalCoefficient}
                    };

                    std::cout << "Publishing technical coefficient: " << coeffPayload.dump() << std::endl;
                    client.publish(config.mqtt_topic, coeffPayload.dump(), config.mqtt_qos, false)->wait();
                } catch (const std::exception& e) {
                    std::cerr << "Error fetching car data: " << e.what() << std::endl;
                }
            }

            std::this_thread::sleep_for(std::chrono::seconds(config.publish_interval));
        }

        std::cout << "Disconnecting from MQTT broker..." << std::endl;
        client.disconnect()->wait();
        std::cout << "Disconnected." << std::endl;

    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }

    return 0;
}