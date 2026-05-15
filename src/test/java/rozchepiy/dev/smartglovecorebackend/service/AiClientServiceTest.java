package rozchepiy.dev.smartglovecorebackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import rozchepiy.dev.smartglovecorebackend.dto.external.AiPredictResponse;
import rozchepiy.dev.smartglovecorebackend.dto.request.PredictRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AiClientService aiClientService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiClientService, "aiServerUrl", "http://localhost:8000");
    }

    @Test
    void initModelOnAiServer_ShouldReturnTrue_WhenResponseIs2xx() {
        String modelId = "test-model";
        String expectedUrl = "http://localhost:8000/models/test-model";

        when(restTemplate.postForEntity(eq(expectedUrl), any(Map.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        boolean result = aiClientService.initModelOnAiServer(modelId);

        assertTrue(result);
    }

    @Test
    void predictGestureOnAiServer_ShouldReturnResponse_WhenValid() {
        PredictRequest request = new PredictRequest();
        request.setModelId("test-model");

        AiPredictResponse mockResponse = new AiPredictResponse();

        when(restTemplate.postForEntity(eq("http://localhost:8000/predict"), eq(request), eq(AiPredictResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        AiPredictResponse result = aiClientService.predictGestureOnAiServer(request);

        assertNotNull(result);
    }

    @Test
    void predictGestureOnAiServer_ShouldThrowException_WhenServerFails() {
        PredictRequest request = new PredictRequest();
        request.setModelId("test-model");

        when(restTemplate.postForEntity(anyString(), any(), eq(AiPredictResponse.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> aiClientService.predictGestureOnAiServer(request));

        assertTrue(exception.getMessage().contains("Помилка розпізнавання жесту"));
    }
}