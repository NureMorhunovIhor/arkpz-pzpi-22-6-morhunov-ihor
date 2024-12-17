package org.nure.atark.autoinsure.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.nure.atark.autoinsure.dto.MeasurementDto;
import org.nure.atark.autoinsure.dto.TechnicalScoreDto;
import org.nure.atark.autoinsure.service.MeasurementService;
import org.nure.atark.autoinsure.service.TechnicalScoreService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class MqttListener {

    private static final String SERVER_URI = "tcp://broker.hivemq.com:1883";
    private static final String CLIENT_ID = "mqtt_java_client";
    private static final String TOPIC = "test/topic";

    private final MeasurementService measurementService;
    private final TechnicalScoreService technicalScoreService;
    private final ObjectMapper objectMapper;

    public MqttListener(MeasurementService measurementService, TechnicalScoreService technicalScoreService, ObjectMapper objectMapper) {
        this.measurementService = measurementService;
        this.technicalScoreService = technicalScoreService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void connectAndSubscribe() {
        try {
            MqttClient client = new MqttClient(SERVER_URI, CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(60);

            System.out.println("Попытка подключения к MQTT брокеру...");
            client.connect(options);
            System.out.println("Подключение успешно!");

            client.subscribe(TOPIC, (topic, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("Получено сообщение: " + payload);

                try {
                    if (!payload.trim().startsWith("{") || !payload.trim().endsWith("}")) {
                        throw new IllegalArgumentException("Сообщение не является JSON");
                    }

                    if (payload.contains("sensorId") && payload.contains("value")) {
                        MeasurementDto measurementDto = objectMapper.readValue(payload, MeasurementDto.class);
                        measurementService.createMeasurement(measurementDto);
                    } else if (payload.contains("carId") && payload.contains("value")) {
                        TechnicalScoreDto technicalScoreDto = objectMapper.readValue(payload, TechnicalScoreDto.class);
                        technicalScoreService.saveTechnicalScore(technicalScoreDto);
                    } else {
                        System.err.println("Неизвестный формат сообщения.");
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка обработки сообщения: " + e.getMessage());
                }
            });

            System.out.println("Подписка на тему: " + TOPIC);

        } catch (MqttException e) {
            System.err.println("Ошибка подключения к MQTT брокеру: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка подключения к MQTT брокеру: " + e.getMessage(), e);
        }
    }

}
