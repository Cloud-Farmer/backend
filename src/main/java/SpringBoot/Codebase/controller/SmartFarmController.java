package SpringBoot.Codebase.controller;

import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.repository.SmartFarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kit")
public class SmartFarmController {

    @Autowired
    private SmartFarmRepository smartFarmRepository;

    @PostMapping("/new")
    public ResponseEntity newKit() {
        // 등록시 condition을 기본값으로

        return new ResponseEntity("키트 등록완료", HttpStatus.OK);
    }

    @GetMapping("/{kit_id}")
    public ResponseEntity getKit(@PathVariable("kit_id") Long kitId) {
        SmartFarm smartFarm = smartFarmRepository.findById(kitId).orElse(null);
        return new ResponseEntity(smartFarm, HttpStatus.OK);
    }

    @PostMapping("/alert/{kit_id}")
    public ResponseEntity setAlert(@PathVariable("kit_id") Long kitID,
                                   @RequestParam("type") String type,
                                   @RequestParam("value") int value) {
        SmartFarm kit = smartFarmRepository.findById(kitID)
                .orElseThrow(() -> {
                    throw new RuntimeException("존재하지 않는 KIT");
                });

        if (type.equals("temperature")) {
            kit.setTemperatureConditionValue(value);
        }

        return new ResponseEntity("", HttpStatus.OK);
    }

    @GetMapping("/alert/{kit_id}")
    public ResponseEntity getAlertCondition(@PathVariable("kid_id") Long kitId) {
        SmartFarm smartFarm = smartFarmRepository.findById(kitId).orElse(null);
        return new ResponseEntity(smartFarm, HttpStatus.OK);
    }
}
