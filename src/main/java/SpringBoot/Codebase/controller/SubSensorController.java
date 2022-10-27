package SpringBoot.Codebase.controller;


import SpringBoot.Codebase.domain.service.SensorService;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SubSensorController {

    private SensorService sensorService;

    @Autowired
    public SubSensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping("/sensor")
    public ResponseEntity requestSensorData(@RequestParam("kit_id") String kitId,
                                            @RequestParam("sensor") String sensor,
                                            @RequestParam("limit") String limit) {
        List<QueryResult.Result> results = new ArrayList<>();

        results = sensorService.selectDataFromSensor(sensor, kitId, limit);

        return new ResponseEntity(results, HttpStatus.OK);
    }
    @GetMapping("/sensor/1m")
    public ResponseEntity requestSensorData(@RequestParam("kit_id") String kitId,
                                            @RequestParam("sensor") String sensor
                                            ) {
        List<QueryResult.Result> results = new ArrayList<>();

        results = sensorService.selectDataSensor(kitId, sensor);

        return new ResponseEntity(results, HttpStatus.OK);
    }
    @GetMapping("/actuator")
    public ResponseEntity requestActuatorData(@RequestParam("kit_id") String kitId,
                                              @RequestParam("sensor") String sensor) {
        try {
            boolean status = sensorService.receivedToActuator(kitId, sensor);

            return new ResponseEntity(status, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/sensors") // TODO : SmartFarm Entity 구현하고 하기
    public ResponseEntity requestSensors(@RequestParam("kit_id") String kitId) {
        return new ResponseEntity("", HttpStatus.OK);
    }
}
