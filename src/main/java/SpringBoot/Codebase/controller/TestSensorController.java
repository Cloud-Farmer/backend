package SpringBoot.Codebase.controller;

import SpringBoot.Codebase.domain.dto.Sensordto;
import SpringBoot.Codebase.domain.service.SensorService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*", allowedHeaders = "*")
public class TestSensorController {

    @Autowired
    private SensorService sensorService;
    @GetMapping("/test")
    @ApiOperation("센서 데이터 출력")
    public @ResponseBody List <Sensordto> sensordtos(){
        return sensorService.SelectSensorData();
    }
    @GetMapping("/test1")
    @ApiOperation("센서 데이터 입력")
    public void writeInflux(){
        sensorService.writeData();
    }

//    @PostMapping("/test2")
//    @ApiOperation("PUB 발행")
//    public void test2() {
//        sensorService.sentToMqtt();
//    }

}
