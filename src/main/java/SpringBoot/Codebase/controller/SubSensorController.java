package SpringBoot.Codebase.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SubSensorController {


    @GetMapping("/sensor")
    public ResponseEntity requestSensorData(@RequestParam("kitid") String kitId,
                                            @RequestParam("sensor") String sensor) { // TODO : 시간 Range도 받아야함



        return new ResponseEntity("", HttpStatus.OK);
    }
}
