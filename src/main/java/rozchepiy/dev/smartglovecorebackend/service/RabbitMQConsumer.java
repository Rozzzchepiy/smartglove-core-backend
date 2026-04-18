package rozchepiy.dev.smartglovecorebackend.service;

import rozchepiy.dev.smartglovecorebackend.config.RabbitMQConfig;
import rozchepiy.dev.smartglovecorebackend.dto.message.TrainResultMessage;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final GestureModelRepository gestureModelRepository;

    @RabbitListener(queues = RabbitMQConfig.TRAIN_RESULTS_QUEUE)
    public void receiveTrainResult(TrainResultMessage result) {

        gestureModelRepository.findById(result.getModelId()).ifPresent(model -> {

            if ("SUCCESS".equalsIgnoreCase(result.getStatus())) {
                model.setStatus(ModelStatus.READY);
                model.setS3PathToKeras(result.getS3KerasPath());
                model.setS3PathToScaler(result.getS3ScalerPath());
                model.setS3PathToLabels(result.getS3LabelsPath());
            }
            else {
                model.setStatus(ModelStatus.FAILED);
            }

            gestureModelRepository.save(model);

        });
    }
}