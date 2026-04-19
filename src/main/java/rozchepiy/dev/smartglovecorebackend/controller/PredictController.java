package rozchepiy.dev.smartglovecorebackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rozchepiy.dev.smartglovecorebackend.dto.external.AiInitRequest;
import rozchepiy.dev.smartglovecorebackend.service.AiClientService;
import rozchepiy.dev.smartglovecorebackend.service.GestureModelService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/predict")
@RequiredArgsConstructor
public class PredictController {

    private final GestureModelService gestureModelService;
    private final AiClientService aiClientService;

    @PostMapping("/init/{modelId}")
    public ResponseEntity<Map<String, String>> initPredict(@PathVariable String modelId) {

        AiInitRequest request = gestureModelService.prepareInitRequest(modelId);

        aiClientService.initModelOnAiServer(request);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Модель успішно завантажена в пам'ять ШІ-сервера. Можна починати розпізнавання."
        ));
    }
}