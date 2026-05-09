package rozchepiy.dev.smartglovecorebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rozchepiy.dev.smartglovecorebackend.dto.external.AiPredictResponse;
import rozchepiy.dev.smartglovecorebackend.dto.request.PredictRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClientService {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public boolean initModelOnAiServer(String modelId) {
        String url = aiServerUrl + "/models/" + modelId;

        log.info("Запит на ініціалізацію моделі {} на AI сервері: {}", modelId, url);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("modelId", modelId);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, requestBody, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("AI сервер успішно ініціалізував модель {}", modelId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Помилка ініціалізації на AI сервері: {}", e.getMessage());
            return false;
        }
    }

    public void deleteModelFromAiServer(String modelId) {
        String url = aiServerUrl + "/models/" + modelId;

        log.info("Запит на видалення моделі {} з пам'яті AI сервера: {}", modelId, url);

        try {
            restTemplate.delete(url);
            log.info("Запит на видалення моделі {} надіслано", modelId);
        } catch (Exception e) {
            log.error("Не вдалося видалити модель з AI сервера: {}", e.getMessage());
        }
    }

    public AiPredictResponse predictGestureOnAiServer(PredictRequest request) {
        String url = aiServerUrl + "/predict";
        log.debug("Відправка жесту на розпізнавання для моделі: {}", request.getModelId());

        try {
            ResponseEntity<AiPredictResponse> response = restTemplate.postForEntity(
                    url,
                    request,
                    AiPredictResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("AI сервер повернув порожню відповідь");
            }
        } catch (Exception e) {
            log.error("Помилка розпізнавання на AI сервері: {}", e.getMessage());
            throw new RuntimeException("Помилка розпізнавання жесту", e);
        }
    }
}