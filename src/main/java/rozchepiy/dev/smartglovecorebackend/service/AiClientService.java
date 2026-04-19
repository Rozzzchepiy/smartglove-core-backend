package rozchepiy.dev.smartglovecorebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rozchepiy.dev.smartglovecorebackend.dto.external.AiInitRequest;
import rozchepiy.dev.smartglovecorebackend.dto.external.AiPredictResponse;
import rozchepiy.dev.smartglovecorebackend.dto.request.PredictRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClientService {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public boolean initModelOnAiServer(AiInitRequest request) {
        String url = aiServerUrl + "/init";

        log.info("Відправка запиту на ініціалізацію моделі до AI сервера: {}", url);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("AI сервер успішно завантажив модель!");
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Помилка зв'язку з AI сервером: {}", e.getMessage());
            throw new RuntimeException("AI сервер недоступний або не зміг завантажити модель", e);
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