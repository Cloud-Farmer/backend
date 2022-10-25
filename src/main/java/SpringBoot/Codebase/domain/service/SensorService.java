package SpringBoot.Codebase.domain.service;


import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.dto.Sensordto;
import SpringBoot.Codebase.domain.measurement.Temperature;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;
import retrofit2.http.HEAD;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.messaging.Message;
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

    public void sentToMqtt(String kitid, String sensor, String available) {
        String topic = kitid + "/actuator/" + "sensor";
        mqttOrderGateway.sendToMqtt(available, topic);
        log.info("topic {} data : {}", topic, available);
    }
}