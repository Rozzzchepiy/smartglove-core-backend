package rozchepiy.dev.smartglovecorebackend.service;

import lombok.extern.slf4j.Slf4j;
import rozchepiy.dev.smartglovecorebackend.config.RabbitMQConfig;
import rozchepiy.dev.smartglovecorebackend.dto.message.TrainResultMessage;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final GestureModelRepository gestureModelRepository;

    @RabbitListener(queues = RabbitMQConfig.TRAIN_RESULTS_QUEUE)
    public void receiveTrainResult(TrainResultMessage result) {
        log.info("Отримано повідомлення з черги про завершення тренування моделі {}. Статус: {}",
                result.getModelId(), result.getStatus());

        gestureModelRepository.findById(result.getModelId()).ifPresent(model -> {

            if ("SUCCESS".equalsIgnoreCase(result.getStatus())) {
                model.setStatus(ModelStatus.READY);
                model.setS3PathToKeras("model_" + model.getId() + ".keras");
                model.setS3PathToScaler("scaler_" + model.getId() + ".pkl");
                model.setS3PathToLabels("labels_" + model.getId() + ".npy");
                log.info("Модель {} успішно оновлена в БД і готова до використання.", model.getId());
            }
            else {
                model.setStatus(ModelStatus.FAILED);
                log.warn("Модель {} отримала статус FAILED від ШІ-сервера.", model.getId());
            }

            gestureModelRepository.save(model);

        });
    }
}