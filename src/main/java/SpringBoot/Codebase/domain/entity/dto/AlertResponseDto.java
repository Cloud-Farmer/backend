package SpringBoot.Codebase.domain.entity.dto;

import SpringBoot.Codebase.domain.entity.Alert;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AlertResponseDto {

    private String subject; // 알람 유형

    private String message; // 알람 내용

    private String status; // 알람 상태

    private String language; // 언어

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime alertedTime;  // 시각

    public static AlertResponseDto of(Alert alert) {
        AlertResponseDto alertResponseDto = new AlertResponseDto();
        alertResponseDto.setAlertedTime(alert.getAlertedTime());
        alertResponseDto.setStatus(alert.getStatus());
        alertResponseDto.setLanguage(alert.getLanguage());
        alertResponseDto.setMessage(alert.getMessage());
        alertResponseDto.setSubject(alert.getSubject());
        return alertResponseDto;
    }
}
