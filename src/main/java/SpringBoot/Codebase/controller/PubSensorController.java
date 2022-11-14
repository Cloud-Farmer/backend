package SpringBoot.Codebase.controller;

import SpringBoot.Codebase.domain.service.ActuatorService;
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
    private final ActuatorService actuatorService;

    public PubSensorController(ActuatorService actuatorService) {
        this.actuatorService = actuatorService;
    }

    @PostMapping("/actuator")
    @ApiOperation("Pub 발행")
    public ResponseEntity sensor(@RequestParam("kitid") String kitId,
                                 @RequestParam("sensor") String sensor,
                                 @RequestParam("available") String available) {

        // topic kitid/actuator/sensor => data available
        actuatorService.sentToMqtt(kitId,sensor,available);
        if (available.equals("1")){
            log.info(kitId+" 번 키트의 "+sensor+" 센서가 켜졌습니다.");
            return new ResponseEntity(kitId+" 번 키트의 "+sensor+" 센서가 켜졌습니다.", HttpStatus.OK);
        }
        else if(available.equals("0")){
            log.info(kitId+" 번 키트의 "+sensor+" 센서가 꺼졌습니다.");
            return new ResponseEntity(kitId+" 번 키트의 "+sensor+" 센서가 꺼졌습니다.", HttpStatus.OK);
        }
        else
            log.info("잘못된 조회입니다.");
            return new ResponseEntity("잘못된 조회입니다.", HttpStatus.BAD_REQUEST);
    }
}
