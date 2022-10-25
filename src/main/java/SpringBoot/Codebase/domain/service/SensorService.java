package SpringBoot.Codebase.domain.service;


import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.dto.Sensordto;
import lombok.extern.slf4j.Slf4j;

import org.influxdb.dto.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;

import org.springframework.stereotype.Service;

@Service@Slf4j
public class SensorService {

    private final static Logger logger = LoggerFactory.getLogger(Sensordto.class.getSimpleName());
    @Autowired
    private MqttConfiguration.MqttOrderGateway mqttOrderGateway;

    private final InfluxDBTemplate<Point> influxDBTemplate;

    public SensorService(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    //private final InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086","admin","12345");

    public void sentToMqtt(String kitId, String sensor, String available) {
        String topic = kitId + "/actuator/" + sensor;
        mqttOrderGateway.sendToMqtt(available, topic);
        log.info("topic {} data : {}", topic, available);
    }
}