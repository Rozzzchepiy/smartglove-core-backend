package rozchepiy.dev.smartglovecorebackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rozchepiy.dev.smartglovecorebackend.dto.external.AiInitRequest;
import rozchepiy.dev.smartglovecorebackend.service.GestureModelService;

@RestController
@RequestMapping("/api/v1/predict")
@RequiredArgsConstructor
public class PredictController {

    private final GestureModelService gestureModelService;

    @PostMapping("/init/{modelId}")
    public ResponseEntity<AiInitRequest> initPredict(@PathVariable String modelId) {
        AiInitRequest request = gestureModelService.prepareInitRequest(modelId);

        return ResponseEntity.ok(request);
    }
}