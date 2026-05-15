package rozchepiy.dev.smartglovecorebackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import rozchepiy.dev.smartglovecorebackend.dto.request.SaveGestureRequest;
import rozchepiy.dev.smartglovecorebackend.model.GestureData;
import rozchepiy.dev.smartglovecorebackend.repository.GestureDataRepository;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestureDataServiceTest {

    @Mock
    private GestureDataRepository gestureDataRepository;
    @Mock
    private GestureModelRepository gestureModelRepository;
    @Mock
    private GestureModelService gestureModelService;

    @InjectMocks
    private GestureDataService gestureDataService;

    @Test
    void saveGestureData_ShouldSave_WhenDataIsValid() {
        String modelId = "model-123";
        SaveGestureRequest request = new SaveGestureRequest();
        request.setLabel("A");

        List<Double> validRow = new ArrayList<>();
        for (int i = 0; i < 18; i++) validRow.add(1.0);
        request.setRawData(List.of(validRow));

        when(gestureModelRepository.existsById(modelId)).thenReturn(true);
        when(gestureDataRepository.save(any(GestureData.class))).thenAnswer(i -> i.getArguments()[0]);

        GestureData result = gestureDataService.saveGestureData(modelId, request);

        assertNotNull(result);
        assertEquals("a", result.getLabel());
        verify(gestureDataRepository, times(1)).save(any(GestureData.class));
    }

    @Test
    void saveGestureData_ShouldThrowException_WhenDataHasWrongDimensions() {
        String modelId = "model-123";
        SaveGestureRequest request = new SaveGestureRequest();
        request.setLabel("A");

        List<Double> invalidRow = new ArrayList<>();
        for (int i = 0; i < 10; i++) invalidRow.add(1.0);
        request.setRawData(List.of(invalidRow));

        when(gestureModelRepository.existsById(modelId)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gestureDataService.saveGestureData(modelId, request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Очікується 18 осей"));
        verify(gestureDataRepository, never()).save(any());
    }
}