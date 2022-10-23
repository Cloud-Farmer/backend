package SpringBoot.Codebase.controller;

import SpringBoot.Codebase.domain.dto.Sensordto;
import SpringBoot.Codebase.domain.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestSensorController {

    @Autowired
    private SensorService sensorService;
    @GetMapping("/test")
    public @ResponseBody List <Sensordto> sensordtos(){
        return sensorService.SelectSensorData();
    }
    @GetMapping("/test1")
    public void writeInflux(){
        sensorService.writeData();
    }
}
