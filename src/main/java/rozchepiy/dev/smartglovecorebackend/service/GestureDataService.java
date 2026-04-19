package rozchepiy.dev.smartglovecorebackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rozchepiy.dev.smartglovecorebackend.dto.request.GestureSummaryDto;
import rozchepiy.dev.smartglovecorebackend.dto.request.SaveGestureRequest;
import rozchepiy.dev.smartglovecorebackend.model.GestureData;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.repository.GestureDataRepository;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestureDataService {

    private final GestureDataRepository gestureDataRepository;
    private final GestureModelRepository gestureModelRepository;
    private final GestureModelService gestureModelService;

    private static final String DEFAULT_MODEL_ID = "DEFAULT_SYSTEM_MODEL";

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

    public List<GestureSummaryDto> getModelGestureSummaries(String modelId) {
        GestureModel model = gestureModelService.getModelById(modelId);

        List<GestureSummaryDto> userSummaries = gestureDataRepository.getGestureSummariesByModelId(modelId);

        if (!model.isIncludesDefaultGestures()) {
            return userSummaries;
        }

        List<GestureSummaryDto> defaultSummaries = gestureDataRepository.getGestureSummariesByModelId(DEFAULT_MODEL_ID);

        Map<String, Long> mergedCounts = defaultSummaries.stream()
                .collect(Collectors.toMap(GestureSummaryDto::getLabel, GestureSummaryDto::getCount));

        for (GestureSummaryDto userSummary : userSummaries) {
            mergedCounts.merge(userSummary.getLabel(), userSummary.getCount(), Long::sum);
        }

        return mergedCounts.entrySet().stream()
                .map(entry -> new GestureSummaryDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }


    public Map<String, List<List<List<Double>>>> getTrainingDataForAi(String modelId) {
        GestureModel model = gestureModelService.getModelById(modelId);

        List<GestureData> allTrainingData = gestureDataRepository.findAllByModelId(modelId);

        if (model.isIncludesDefaultGestures()) {
            List<GestureData> defaultData = gestureDataRepository.findAllByModelId(DEFAULT_MODEL_ID);
            allTrainingData.addAll(defaultData);
        }

        Map<String, List<List<List<Double>>>> dictionary = new HashMap<>();
        for (GestureData data : allTrainingData) {
            dictionary.computeIfAbsent(data.getLabel(), k -> new ArrayList<>())
                    .add(data.getRawData());
        }

        return dictionary;
    }
}
