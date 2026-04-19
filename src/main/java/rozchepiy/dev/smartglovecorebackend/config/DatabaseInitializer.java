package rozchepiy.dev.smartglovecorebackend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import rozchepiy.dev.smartglovecorebackend.model.GestureData;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;
import rozchepiy.dev.smartglovecorebackend.repository.GestureDataRepository;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final GestureModelRepository gestureModelRepository;
    private final GestureDataRepository gestureDataRepository;
    private final MinioClient minioClient; // Клієнт для роботи з S3 сховищем

    @Value("${minio.bucket.name}")
    private String bucketName;

    public static final String DEFAULT_MODEL_ID = "DEFAULT_SYSTEM_MODEL";

    @Override
    public void run(String... args) {
        if (!gestureModelRepository.existsById(DEFAULT_MODEL_ID)) {
            log.info("Розпочинаю повну ініціалізацію системи (Cold Start)...");

            initDefaultModelRecord();
            seedDefaultGestures();
            seedMinioFiles();

            log.info("Ініціалізація системи успішно завершена!");
        } else {
            log.info("Дефолтна модель та дані вже присутні. Ініціалізація пропущена (Hot Start).");
        }
    }

    private void initDefaultModelRecord() {
        log.info("Крок 1: Створення дефолтної моделі в БД...");
        GestureModel defaultModel = GestureModel.builder()
                .id(DEFAULT_MODEL_ID)
                .name("Базова модель (System)")
                .isDefault(true)
                .includesDefaultGestures(false) // Сама в собі нічого не інклюдить
                .status(ModelStatus.READY)
                .s3PathToKeras("models/default_model.keras")
                .s3PathToScaler("models/default_scaler.pkl")
                .s3PathToLabels("models/default_labels.npy")
                .build();

        gestureModelRepository.save(defaultModel);
    }

    private void seedDefaultGestures() {
        log.info("Крок 2: Завантаження дефолтних жестів з JSON файлу...");
        try {
            InputStream inputStream = new ClassPathResource("data/default_gestures.json").getInputStream();

            ObjectMapper objectMapper = new ObjectMapper();

            TypeReference<Map<String, List<List<List<Double>>>>> typeReference = new TypeReference<>() {};
            Map<String, List<List<List<Double>>>> jsonMap = objectMapper.readValue(inputStream, typeReference);

            List<GestureData> dataToSave = new ArrayList<>();

            for (Map.Entry<String, List<List<List<Double>>>> entry : jsonMap.entrySet()) {
                String label = entry.getKey();
                for (List<List<Double>> rawDataInstance : entry.getValue()) {
                    GestureData gestureData = GestureData.builder()
                            .modelId(DEFAULT_MODEL_ID)
                            .label(label)
                            .rawData(rawDataInstance)
                            .build();
                    dataToSave.add(gestureData);
                }
            }

            gestureDataRepository.saveAll(dataToSave);
            log.info("Успішно завантажено {} екземплярів жестів у БД.", dataToSave.size());

        } catch (Exception e) {
            log.error("Помилка парсингу default_gestures.json. Переконайся, що файл лежить у src/main/resources/data/", e);
        }
    }

    private void seedMinioFiles() {
        log.info("Крок 3: Завантаження фізичних файлів ваг у MinIO...");
        uploadToMinio("models/default_model.keras", "data/weights/default_model.keras");
        uploadToMinio("models/default_scaler.pkl", "data/weights/default_scaler.pkl");
        uploadToMinio("models/default_labels.npy", "data/weights/default_labels.npy");
    }

    private void uploadToMinio(String s3Path, String resourcePath) {
        try (InputStream is = new ClassPathResource(resourcePath).getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(s3Path)
                            .stream(is, is.available(), -1)
                            .build()
            );
            log.info("Файл {} успішно завантажено в MinIO.", s3Path);
        } catch (Exception e) {
            log.error("Помилка завантаження файлу {} у MinIO. Переконайся, що він є у папці resources.", resourcePath, e);
        }
    }
}