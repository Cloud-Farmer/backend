package SpringBoot.Codebase.controller;


import SpringBoot.Codebase.domain.service.ActuatorService;
import SpringBoot.Codebase.domain.service.SensorService;
import com.influxdb.query.FluxRecord;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SubSensorController {

    private final SensorService sensorService;
    private final ActuatorService actuatorService;

    @Autowired
    public SubSensorController(SensorService sensorService,ActuatorService actuatorService) {
        this.sensorService = sensorService;
        this.actuatorService = actuatorService;
    }


    @GetMapping("/sensor")
    public ResponseEntity requestSensorDataWithDate(@RequestParam("kit_id") String kitId,
                                            @RequestParam("sensor") String sensor,
                                            @RequestParam("date") String date
    ){ //1m, 7d, 30d
        List<FluxRecord> results = new ArrayList<>();
        results = sensorService.selectDataSensor(kitId, sensor,date);
        return new ResponseEntity(results, HttpStatus.OK);
    }
    @GetMapping("/actuator")
    public ResponseEntity requestActuatorData(@RequestParam("kit_id") String kitId,
                                              @RequestParam("sensor") String sensor,
                                              @RequestParam("date")String date) {
        try {
            List<FluxRecord> results = new ArrayList<>();
            results = actuatorService.selectActuator(kitId, sensor, date);
            return new ResponseEntity(results, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/sensors") // TODO : SmartFarm Entity 구현하고 하기
    public ResponseEntity requestSensors(@RequestParam("kit_id") String kitId) {
        return new ResponseEntity("", HttpStatus.OK);
    }
}

