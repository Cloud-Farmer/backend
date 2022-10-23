package SpringBoot.Codebase.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.influxdb.annotation.Column;

@Getter
@Setter
@Builder
public class NewUserdto {

    private String username;
    private String identity;
    private String farmName;
    private String password;
}
