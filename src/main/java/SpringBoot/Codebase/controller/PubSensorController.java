package SpringBoot.Codebase.controller;

import SpringBoot.Codebase.domain.service.SensorService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api")
@RestController
@CrossOrigin(origins="*", allowedHeaders = "*")
public class PubSensorController {
    private final SensorService sensorService;

    public PubSensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/actuator")
    @ApiOperation("Pub 발행")
    public ResponseEntity sensor(@RequestParam("kitid") String kitId,
                                 @RequestParam("sensor") String sensor,
                                 @RequestParam("available") String available) {
        String temp = kitId + " " + sensor + " " + available;
        // topic kitid/actuator/sensor => data available
        sensorService.sentToMqtt(kitId,sensor,available);
        return new ResponseEntity(temp, HttpStatus.OK);
    }
}
