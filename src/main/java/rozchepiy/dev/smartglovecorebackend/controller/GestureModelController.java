package rozchepiy.dev.smartglovecorebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rozchepiy.dev.smartglovecorebackend.dto.request.CreateModelRequest;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.service.GestureModelService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class GestureModelController {

    private final GestureModelService gestureModelService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GestureModel>> getUserModels(@PathVariable String userId) {
        List<GestureModel> models = gestureModelService.getUserModels(userId);
        return ResponseEntity.ok(models);
    }

    @PostMapping
    public ResponseEntity<GestureModel> createModel(@Valid @RequestBody CreateModelRequest request) {
        GestureModel createdModel = gestureModelService.createModel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdModel);
    }

    @PostMapping("/{modelId}/train")
    public ResponseEntity<Void> startTraining(@PathVariable String modelId) {
        gestureModelService.startTraining(modelId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping("/{modelId}")
    public ResponseEntity<GestureModel> getModel(@PathVariable String modelId) {
        GestureModel model = gestureModelService.getModelById(modelId);
        return ResponseEntity.ok(model);
    }
}