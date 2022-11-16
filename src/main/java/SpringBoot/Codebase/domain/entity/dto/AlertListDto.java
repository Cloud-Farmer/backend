package SpringBoot.Codebase.domain.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AlertListDto {

    List<AlertResponseDto> alertResponseDtoList;

    Long totalElements;

    int totalPages;
}
