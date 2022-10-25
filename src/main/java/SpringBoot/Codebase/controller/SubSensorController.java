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

        List<QueryResult.Result> results = sensorService.selectDataFromTemperature(sensor, limit);

        return new ResponseEntity(results, HttpStatus.OK);
    }
}
