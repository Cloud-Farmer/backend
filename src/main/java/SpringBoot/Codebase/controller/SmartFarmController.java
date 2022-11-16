package SpringBoot.Codebase.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kit")
public class SmartFarmController {

    @PostMapping("/new")
    public ResponseEntity newKit() {

        return new ResponseEntity("키드 등록완료", HttpStatus.OK);
    }

    @GetMapping("/{kit_id")
    public ResponseEntity getKit(@PathVariable("kit_id") String kitID) {
        
        return new ResponseEntity(kitID, HttpStatus.OK);
    }

}
