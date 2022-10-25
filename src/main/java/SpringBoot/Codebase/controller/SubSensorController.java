package SpringBoot.Codebase.controller;

import SpringBoot.Codebase.domain.measurement.Temperature;
import SpringBoot.Codebase.domain.service.SensorService;
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

//    private SensorService sensorService;
//
//    @Autowired
//    public SubSensorController(SensorService sensorService) {
//        this.sensorService = sensorService;
//    }
//
//    @GetMapping("/sensor")
//    public ResponseEntity requestSensorData(@RequestParam("kitid") String kitId,
//                                            @RequestParam("sensor") String sensor) { // TODO : 시간 Range도 받아야함
//
//        List<Temperature> temperatures = sensorService.selectDataFromTemperature(sensor);
//
//        return new ResponseEntity(temperatures, HttpStatus.OK);
//    }
}
