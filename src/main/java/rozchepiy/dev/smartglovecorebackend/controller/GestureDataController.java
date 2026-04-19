package rozchepiy.dev.smartglovecorebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rozchepiy.dev.smartglovecorebackend.dto.request.SaveGestureRequest;
import rozchepiy.dev.smartglovecorebackend.model.GestureData;
import rozchepiy.dev.smartglovecorebackend.service.GestureDataService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/models/{modelId}/gestures")
@RequiredArgsConstructor
public class GestureDataController {

    private final GestureDataService gestureDataService;

    @PostMapping
    public ResponseEntity<GestureData> addGestureToModel(
            @PathVariable String modelId,
            @Valid @RequestBody SaveGestureRequest request) {

        GestureData savedData = gestureDataService.saveGestureData(modelId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedData);
    }
    @GetMapping("/internal/data")
    public ResponseEntity<Map<String, List<List<Double>>>> getTrainingDataForAi(@PathVariable String modelId) {
        Map<String, List<List<Double>>> result = gestureDataService.getFormattedTrainingData(modelId);
        return ResponseEntity.ok(result);
    }
}