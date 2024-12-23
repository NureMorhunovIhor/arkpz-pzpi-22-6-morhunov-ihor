package org.nure.atark.autoinsure.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
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
    private final ObjectMapper objectMapper;
    private final TechnicalScoreService technicalScoreService;

    private MqttClient client;

    public MqttListener(MeasurementService measurementService,TechnicalScoreService technicalScoreService, ObjectMapper objectMapper) {
        this.measurementService = measurementService;
        this.objectMapper = objectMapper;
        this.technicalScoreService = technicalScoreService;
    }

    @PostConstruct
    public void connectAndSubscribe() {
        try {
            client = new MqttClient(SERVER_URI, CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(60);

            System.out.println("Попытка подключения к MQTT брокеру...");
            client.connect(options);
            System.out.println("Подключение успешно!");

            client.subscribe(TOPIC, 1, (topic, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("Получено сообщение: " + payload);

                try {
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
