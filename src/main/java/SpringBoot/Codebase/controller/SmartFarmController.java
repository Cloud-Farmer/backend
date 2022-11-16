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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        System.out.println(" = " + flowContext.getRegistry());
        return this.flowContext.registration(flow).register();
    }

    private void removeAdapter(String mqttId) {
        this.flowContext.remove(mqttId);    // 어댑터 삭제
    }

    @PostMapping("/new")
    @ApiOperation("kit 동적 생성 ")
    public ResponseEntity newKit(@RequestParam String kitId) {
        // 등록시 condition을 기본값으로
            // id a5423b DB 저장하고
        IntegrationFlowRegistration registration = addAdapter(kitId+"/json");
        String id = registration.getId();
        SmartFarm smartFarm = new SmartFarm();
        smartFarm.setId(4L);
        smartFarm.setMqttAdapterId(kitId);
        smartFarm.setHumidityConditionValue(100);
        smartFarm.setIlluminanceConditionValue(100);
        smartFarm.setSoilHumidityConditionValue(100);
        smartFarm.setTemperatureConditionValue(100);
        smartFarmRepository.save(smartFarm);
        return new ResponseEntity("키트 등록완료", HttpStatus.OK);
    }

    @DeleteMapping("/delete") // TODO : KIT ID를 받아야함
    @ApiOperation("kid delete")
    public ResponseEntity deleteKit(@RequestParam String kitId) {
        SmartFarm smartFarm = smartFarmRepository.findByMqttAdapterId(kitId)
                .orElseThrow(()->{
                    throw new RuntimeException("KitId가 존재하지 않음");
        });
        int s = Integer.parseInt(smartFarm.getMqttAdapterId());
        // SmratFarm DB 데이터도 삭제
        removeAdapter("org.springframework.integration.dsl.StandardIntegrationFlow#"+String.valueOf(s-1));
        return new ResponseEntity("키트 삭제", HttpStatus.OK);
    }

    @GetMapping("/{kit_id}")
    public ResponseEntity getKit(@PathVariable("kit_id") Long kitId) {
        SmartFarm smartFarm = smartFarmRepository.findById(kitId).orElse(null);
        return new ResponseEntity(smartFarm, HttpStatus.OK);
    }

    // 대시보드에서 추가하는거는 작동중인 KIT 우리 서비스에 등록하는것.
    // 작동중이 우리 서버로 연결에
    // 비동기 -> 작동중인 KIT (ID Generated, a5423b) -> Broker -> Backend -> Fronm (Kit 등록 -> id : a5423b)
    // KIT 고유아이디는

    @ApiOperation(value = "KIT ID를 가지는 키트의 알람 트리거 설정", notes = "type(센서 유형), value (트리거가 될 값) 현재는 온도 >= value 임 \n 즉 측정된 온도가 value 이상이면 알람 발생")
    @PostMapping("/alert/{kit_id}")
    public ResponseEntity setAlert(@PathVariable("kit_id") Long kitID,
                                   @RequestParam("type") String type,
                                   @RequestParam("value") int value) {
        SmartFarm kit = smartFarmRepository.findById(kitID)
                .orElseThrow(() -> {
                    throw new RuntimeException("존재하지 않는 KIT");
                });
        boolean typeCheck = false;
        if (type.equals("temperature")) {
            kit.setTemperatureConditionValue(value);
            typeCheck = true;
        } else if (type.equals("soilhumidity")) {
            kit.setSoilHumidityConditionValue(value);
            typeCheck = true;

        } else if (type.equals("illuminance")) {
            kit.setIlluminanceConditionValue(value);
            typeCheck = true;

        } else if (type.equals("humidity")) {
            kit.setHumidityConditionValue(value);
            typeCheck = true;
        }

        if (typeCheck) {
            smartFarmRepository.save(kit);
            sentToMqtt(kitID, type, value);
        }

        return new ResponseEntity(type + ":" + value + " 으로 지정됨", HttpStatus.OK);
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
