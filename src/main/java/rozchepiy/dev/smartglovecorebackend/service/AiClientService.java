package rozchepiy.dev.smartglovecorebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rozchepiy.dev.smartglovecorebackend.dto.external.AiInitRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiClientService {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public boolean initModelOnAiServer(AiInitRequest request) {
        String url = aiServerUrl + "/api/v1/predict/init";

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
}