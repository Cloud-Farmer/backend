package SpringBoot.Codebase.controller;


import SpringBoot.Codebase.domain.dto.NewUserdto;
import SpringBoot.Codebase.domain.dto.Userdto;
import SpringBoot.Codebase.domain.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestUserController {

    private UserService userService;
    @Autowired
    public TestUserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    @ApiOperation("전체 유저 조회")
    public ResponseEntity getAllUsers() {
        try {
            List<Userdto> user = userService.getAllUsers();
            return new ResponseEntity(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("잘못된 조회", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/users/new")
    @ApiOperation("유저 생성")
    public ResponseEntity addNewUser(@RequestBody NewUserdto userDto) {
        try {
            userService.newUser(userDto);
            return new ResponseEntity("생성되었습니다", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("잘못된 생성", HttpStatus.BAD_REQUEST);
        }
    }

}
