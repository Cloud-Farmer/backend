package SpringBoot.Codebase.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class Userdto {

    private String username;
    private String identity;
    private String farmName;
}

