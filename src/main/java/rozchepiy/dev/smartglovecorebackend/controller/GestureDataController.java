package rozchepiy.dev.smartglovecorebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rozchepiy.dev.smartglovecorebackend.dto.request.GestureSummaryDto;
import rozchepiy.dev.smartglovecorebackend.dto.request.SaveGestureRequest;
import rozchepiy.dev.smartglovecorebackend.model.GestureData;
import rozchepiy.dev.smartglovecorebackend.service.GestureDataService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GestureDataController {

    private final GestureDataService gestureDataService;

    @PostMapping("/models/{modelId}/gestures")
    public ResponseEntity<GestureData> addGestureToModel(
            @PathVariable String modelId,
            @Valid @RequestBody SaveGestureRequest request) {

        GestureData savedData = gestureDataService.saveGestureData(modelId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedData);
    }

    @GetMapping("/models/{modelId}/gestures/summary")
    public ResponseEntity<List<GestureSummaryDto>> getGestureSummaries(@PathVariable String modelId) {
        return ResponseEntity.ok(gestureDataService.getModelGestureSummaries(modelId));
    }

    @GetMapping("/internal/models/{modelId}/training-data")
    public ResponseEntity<Map<String, List<List<List<Double>>>>> getTrainingData(@PathVariable String modelId) {
        return ResponseEntity.ok(gestureDataService.getTrainingDataForAi(modelId));
    }
}