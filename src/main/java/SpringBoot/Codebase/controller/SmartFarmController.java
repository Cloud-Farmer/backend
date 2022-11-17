package SpringBoot.Codebase.controller;

import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.entity.Alert;
import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.entity.dto.AlertListDto;
import SpringBoot.Codebase.domain.entity.dto.AlertResponseDto;
import SpringBoot.Codebase.domain.repository.AlertRepository;
import SpringBoot.Codebase.domain.repository.SmartFarmRepository;
import io.swagger.annotations.ApiOperation;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.context.IntegrationFlowContext.IntegrationFlowRegistration;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/kit")
public class SmartFarmController {

    @Autowired
    private SmartFarmRepository smartFarmRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private MqttConfiguration.MqttOrderGateway mqttOrderGateway;

    @Autowired
    private IntegrationFlowContext flowContext;

    @Autowired
    private MessageChannel mqttInputChannel;

    @Value("${mqtt.url}")
    String BROKER_URL;
    private IntegrationFlowRegistration addAdapter(String... topics) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(BROKER_URL, MqttAsyncClient.generateClientId(), topics);
        StandardIntegrationFlow flow = IntegrationFlows.from(adapter)
                .channel(mqttInputChannel)
                .get();
        return this.flowContext.registration(flow).register();
    }
    private void removeAdapter(String mqttId) {
        this.flowContext.remove(mqttId);    // 어댑터 삭제
    }

    @GetMapping("/")
    @ApiOperation("전체 KIT 조회")
    public ResponseEntity getAllKit() {
        List<SmartFarm> farms = smartFarmRepository.findAll();
        return new ResponseEntity(farms, HttpStatus.OK);
    }

    @PostMapping("/new")
    @ApiOperation("kit 동적 생성 ")
    public ResponseEntity newKit(@RequestParam String kitId) {
        try {
            Optional<SmartFarm> farm = smartFarmRepository.findById(Long.valueOf(kitId));
            if(farm.isPresent()){
                throw new RuntimeException("이미 추가된 KIT 입니다");
            }

            SmartFarm smartFarm = new SmartFarm();
            smartFarm.setId(Long.valueOf(kitId));
            smartFarm.setHumidityConditionValue(100);
            smartFarm.setIlluminanceConditionValue(100);
            smartFarm.setSoilHumidityConditionValue(100);
            smartFarm.setTemperatureConditionValue(100);
            smartFarm.setCreatedTime(LocalDateTime.now());

            IntegrationFlowRegistration registration = addAdapter(kitId + "/json");
            smartFarm.setMqttAdapterId(registration.getId());

            smartFarmRepository.save(smartFarm);
            return new ResponseEntity(kitId + " 키트 등록완료", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete") // TODO : KIT ID를 받아야함
    @ApiOperation("kid delete")
    public ResponseEntity deleteKit(@RequestParam String kitId) {
        try {
            SmartFarm smartFarm = smartFarmRepository.findById(Long.valueOf(kitId))
                    .orElseThrow(()->{
                        throw new RuntimeException("해당 키트가 존재하지 않습니다");
                    });

            if (!smartFarm.getMqttAdapterId().equals("adapter control by server")) {
                removeAdapter(smartFarm.getMqttAdapterId());
            }

            smartFarmRepository.delete(smartFarm);
            // SmratFarm DB 데이터도 삭제
            return new ResponseEntity(kitId + " 키트 삭제완료", HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{kit_id}")
    public ResponseEntity getKit(@PathVariable("kit_id") Long kitId) {
        try {
            SmartFarm smartFarm = smartFarmRepository.findById(kitId).orElseThrow(() -> {
                throw new RuntimeException("해당 키트가 존재하지 않습니다");
            });
            return new ResponseEntity(smartFarm, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "KIT ID를 가지는 키트의 알람 트리거 설정", notes = "type(센서 유형), value (트리거가 될 값) 현재는 온도 >= value 임 \n 즉 측정된 온도가 value 이상이면 알람 발생")
    @PostMapping("/alert/{kit_id}")
    public ResponseEntity setAlert(@PathVariable("kit_id") Long kitID,
                                   @RequestParam("type") String type,
                                   @RequestParam("value") int value) {
        try {
            SmartFarm kit = smartFarmRepository.findById(kitID)
                    .orElseThrow(() -> {
                        throw new RuntimeException("존재하지 않는 KIT");
                    });

            boolean typeCheck = true;
            if (type.equals("temperature")) {
                kit.setTemperatureConditionValue(value);
            } else if (type.equals("soilhumidity")) {
                kit.setSoilHumidityConditionValue(value);
            } else if (type.equals("illuminance")) {
                kit.setIlluminanceConditionValue(value);
            } else if (type.equals("humidity")) {
                kit.setHumidityConditionValue(value);
            } else {
                throw new RuntimeException("잘못된 Type 입니다");
            }

            smartFarmRepository.save(kit);
            sentToMqtt(kitID, type, value);

            return new ResponseEntity(type + ":" + value + " 으로 지정됨", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/alert/{kit_id}")
    public ResponseEntity getAlertCondition(@PathVariable("kit_id") Long kitId, @RequestParam("page") int page, @RequestParam("size") int size) {
        SmartFarm smartFarm = smartFarmRepository.findById(kitId).orElse(null);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("alertedTime").descending());

        Page<Alert> alerts = alertRepository.findBySmartFarm(smartFarm, pageRequest);
        List<AlertResponseDto> dtos = new ArrayList<>();
        AlertListDto listDto = new AlertListDto();

        for (Alert alert : alerts) {
            AlertResponseDto dto = AlertResponseDto.of(alert);
            dtos.add(dto);
        }

        listDto.setAlertResponseDtoList(dtos);
        listDto.setTotalElements(alerts.getTotalElements());
        listDto.setTotalPages(alerts.getTotalPages());

        return new ResponseEntity(listDto, HttpStatus.OK);
    }

    public void sentToMqtt(Long kitId, String alertType, int value) {
        String topic = kitId + "/alertvalue/" + alertType;
        mqttOrderGateway.sendToMqtt(String.valueOf(value), topic);
    }
}
