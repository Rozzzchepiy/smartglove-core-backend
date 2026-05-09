package rozchepiy.dev.smartglovecorebackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rozchepiy.dev.smartglovecorebackend.dto.message.TrainTaskMessage;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.model.User;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;
import rozchepiy.dev.smartglovecorebackend.repository.GestureDataRepository;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;
import rozchepiy.dev.smartglovecorebackend.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestureModelService {

    private final GestureModelRepository gestureModelRepository;
    private final GestureDataRepository gestureDataRepository;
    private final UserRepository userRepository;
    private final RabbitMQProducer rabbitMQProducer;
    private final AiClientService aiClientService;

    public GestureModel createModel(String modelName, String userEmail, boolean includesDefault) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        GestureModel newModel = GestureModel.builder()
                .name(modelName)
                .userId(user.getId())
                .includesDefaultGestures(includesDefault)
                .status(ModelStatus.CREATED)
                .isDefault(false)
                .build();

        return gestureModelRepository.save(newModel);
    }

    public List<GestureModel> getUserModels(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        return gestureModelRepository.findByUserIdOrIsDefaultTrue(user.getId());
    }

    public void startTraining(String modelId) {
        GestureModel model = getModelById(modelId);

        model.setStatus(ModelStatus.TRAINING);
        gestureModelRepository.save(model);

        TrainTaskMessage message = TrainTaskMessage.builder()
                .modelId(model.getId())
                .build();

        rabbitMQProducer.sendTrainTask(message);
    }

    public GestureModel getModelById(String id) {
        return gestureModelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Модель не знайдена"));
    }

    public List<GestureModel> getAllModels() {
        return gestureModelRepository.findAll();
    }


    public void initModelForPrediction(String modelId) {
        GestureModel model = getModelById(modelId);

        if (model.getStatus() != ModelStatus.READY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Модель ще не готова (статус: " + model.getStatus() + ")");
        }

        boolean success = aiClientService.initModelOnAiServer(modelId);

        if (!success) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не вдалося завантажити модель на AI сервері");
        }
    }

    public void deleteModel(String modelId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));

        GestureModel model = getModelById(modelId);

        if (model.isDefault()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Системну модель не можна видалити");
        }

        if (!model.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ви не маєте прав на видалення цієї моделі");
        }

        gestureDataRepository.deleteAllByModelId(modelId);

        gestureModelRepository.deleteById(modelId);

        aiClientService.deleteModelFromAiServer(modelId);
    }
}