package rozchepiy.dev.smartglovecorebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rozchepiy.dev.smartglovecorebackend.dto.request.SaveGestureRequest;
import rozchepiy.dev.smartglovecorebackend.model.GestureData;
import rozchepiy.dev.smartglovecorebackend.repository.GestureDataRepository;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GestureDataService {

    private final GestureDataRepository gestureDataRepository;
    private final GestureModelRepository gestureModelRepository;

    public GestureData saveGestureData(String modelId, SaveGestureRequest request) {

        boolean modelExists = gestureModelRepository.existsById(modelId);
        if (!modelExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Модель з ID " + modelId + " не знайдена");
        }

        if (!request.getRawData().isEmpty() && request.getRawData().get(0).size() != 18) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Очікується 18 осей (колонок) у даних");
        }

        GestureData gestureData = GestureData.builder()
                .modelId(modelId)
                .label(request.getLabel().toLowerCase().trim())
                .rawData(request.getRawData())
                .build();

        return gestureDataRepository.save(gestureData);
    }

    public List<GestureData> getAllByModelId(String modelId) {
        return gestureDataRepository.findAllByModelId(modelId);
    }

    public Map<String, List<List<Double>>> getFormattedTrainingData(String modelId) {

        List<GestureData> allData = gestureDataRepository.findAllByModelId(modelId);
        Map<String, List<List<Double>>> formattedData = new HashMap<>();

        for (GestureData data : allData) {
            formattedData.computeIfAbsent(data.getLabel(), k -> new ArrayList<>())
                    .addAll(data.getRawData());
        }

        return formattedData;
    }
}
