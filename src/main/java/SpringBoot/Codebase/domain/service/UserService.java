package SpringBoot.Codebase.domain.service;

import SpringBoot.Codebase.domain.dto.NewUserdto;
import SpringBoot.Codebase.domain.dto.Userdto;
import SpringBoot.Codebase.domain.entity.User;
import SpringBoot.Codebase.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserService {

    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    public List<Userdto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Userdto> dtos = new ArrayList<>();
        for (User item : users) {
           Userdto dto = Userdto
                   .builder()
                   .farmName(item.getFarmname())
                   .username(item.getUsername())
                   .identity(item.getIdentity())
                   .build();

            dtos.add(dto);
            //log.info("조회된 유저 농장명 : {}, 이름 : {} 아이디 : {}",dto.getFarmName(),dto.getUsername(),dto.getIdentity());
        }
        return dtos;
    }
    public void newUser(NewUserdto dto) {
        User newUser = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .identity(dto.getIdentity())
                .farmname(dto.getFarmName())
                .build();
        userRepository.save(newUser);
    }
}
