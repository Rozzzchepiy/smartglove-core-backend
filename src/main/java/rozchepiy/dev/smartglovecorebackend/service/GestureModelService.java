package rozchepiy.dev.smartglovecorebackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rozchepiy.dev.smartglovecorebackend.dto.external.AiInitRequest;
import rozchepiy.dev.smartglovecorebackend.dto.message.TrainTaskMessage;
import rozchepiy.dev.smartglovecorebackend.dto.request.CreateModelRequest;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GestureModelService {
    private final GestureModelRepository gestureModelRepository;
    private final RabbitMQProducer rabbitMQProducer;
    private final MinioService minioService;


    public GestureModel createModel(CreateModelRequest createModelRequest){
        GestureModel gestureModel = GestureModel.builder()
                .userId(createModelRequest.getUserId())
                .name(createModelRequest.getName())
                .isDefault(false)
                .status(ModelStatus.CREATED)
                .build();
        return gestureModelRepository.save(gestureModel);
    }

    public List<GestureModel> getUserModels(String userId) {
        List<GestureModel> defaultModels = gestureModelRepository.findAllByIsDefaultTrue();
        List<GestureModel> userModels = gestureModelRepository.findAllByUserId(userId);
        defaultModels.addAll(userModels);
        return defaultModels;
    }

    public void startTraining(String modelId) {

        GestureModel model = gestureModelRepository.findById(modelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Модель не знайдена"));

        model.setStatus(ModelStatus.TRAINING);
        gestureModelRepository.save(model);

        TrainTaskMessage message = TrainTaskMessage.builder()
                .taskId(UUID.randomUUID().toString())
                .modelId(model.getId())
                .userId(model.getUserId())
                .build();

        rabbitMQProducer.sendTrainTask(message);
    }
    public GestureModel getModelById(String id) {
        return gestureModelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Модель не знайдена"));
    }

    public AiInitRequest prepareInitRequest(String modelId) {
        GestureModel model = getModelById(modelId);

        if (model.getStatus() != ModelStatus.READY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Модель ще не готова (статус: " + model.getStatus() + ")");
        }

        return AiInitRequest.builder()
                .modelId(model.getId())
                .modelUrl(minioService.generatePresignedUrl(model.getS3PathToKeras()))
                .scalerUrl(minioService.generatePresignedUrl(model.getS3PathToScaler()))
                .labelsUrl(minioService.generatePresignedUrl(model.getS3PathToLabels()))
                .build();
    }

    public List<GestureModel> getAllModels() {
        return gestureModelRepository.findAll();
    }
}
