package SpringBoot.Codebase.controller;

import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.entity.Alert;
import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.entity.dto.AlertListDto;
import SpringBoot.Codebase.domain.entity.dto.AlertResponseDto;
import SpringBoot.Codebase.domain.entity.dto.SmartFarmDto;
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
    public ResponseEntity getAllKit(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<SmartFarm> farms = smartFarmRepository.findAll(pageRequest);
        List<SmartFarmDto> smartFarmDtos = new ArrayList<>();

        for (SmartFarm smartFarm : farms) {
            SmartFarmDto dto = SmartFarmDto.of(smartFarm);
            smartFarmDtos.add(dto);
        }

        return new ResponseEntity(smartFarmDtos, HttpStatus.OK);
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
            smartFarm.setAutoMode(0);

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
            return new ResponseEntity(SmartFarmDto.of(smartFarm), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "KIT ID를 가지는 sensor의 알람 트리거 설정", notes = "sensor(센서 유형), value (트리거가 될 값) 현재는 온도 >= value 임 \n 즉 측정된 온도가 value 이상이면 알람 발생")
    @PostMapping("/alert/{kit_id}")
    public ResponseEntity setAlert(@PathVariable("kit_id") Long kitID,
                                   @RequestParam("sensor") String sensor,
                                   @RequestParam("value") int value) {
        try {
            SmartFarm kit = smartFarmRepository.findById(kitID)
                    .orElseThrow(() -> {
                        throw new RuntimeException("존재하지 않는 KIT");
                    });

            if (sensor.equals("temperature")) {
                kit.setTemperatureConditionValue(value);
            } else if (sensor.equals("soilhumidity")) {
                kit.setSoilHumidityConditionValue(value);
            } else if (sensor.equals("illuminance")) {
                kit.setIlluminanceConditionValue(value);
            } else if (sensor.equals("humidity")) {
                kit.setHumidityConditionValue(value);
            } else {
                throw new RuntimeException("잘못된 Sensor 입니다");
            }

            smartFarmRepository.save(kit);
            sentToMqtt(kitID, sensor, value);

            return new ResponseEntity(sensor + "가" + value + "이상이면 알람이 발생됨", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/alert/{kit_id}")
    @ApiOperation("kit 알람 로그 가져오기")
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
    @PostMapping("/{kit_id}/auto")
    @ApiOperation(value="kit 센서 값 자동/수동 제어 토픽 발행",notes="수동=0, 자동=1")
    public ResponseEntity autoMode(@PathVariable("kit_id") Long kitId, @RequestParam int value){
        try{
           SmartFarm smartFarm = smartFarmRepository.findById(kitId)
                   .orElseThrow(()->{
                     throw new RuntimeException("키트가 존재하지 않습니다.");
                   });
           sentToMqtt(kitId,value);
            String mode = "";
            if (value==1){
                mode ="자동 제어";
            }
            else if (value == 0) {
                mode="수동 제어";
            }
            else{
                throw new RuntimeException("잘못된 요청입니다.");
            }
            return new ResponseEntity(kitId+"번 "+mode +" 활성화됨",HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity("0 또는 1을 입력하세요",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{kit_id}/auto")
    @ApiOperation("해당 키트의 auto 상태 가져오기")
    public ResponseEntity getAutoMode(@PathVariable("kit_id")Long kitId){
        try{
            SmartFarm smartFarm = smartFarmRepository.findById(kitId)
                    .orElseThrow(()->{
                        throw new RuntimeException("키트가 존재하지 않습니다.");
                    });

            return new ResponseEntity(smartFarm.getAutoMode(),HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity("잘못된 조회",HttpStatus.BAD_REQUEST);
        }
    }


    public void sentToMqtt(Long kitId, String alertType, int value) {
        String topic = kitId + "/alertvalue/" + alertType;
        mqttOrderGateway.sendToMqtt(String.valueOf(value), topic);
    }
    public void sentToMqtt(Long kitId,int value) {
        String topic = kitId + "/auto";
        mqttOrderGateway.sendToMqtt(String.valueOf(value), topic);
    }
}
