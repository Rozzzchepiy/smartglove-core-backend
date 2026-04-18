package rozchepiy.dev.smartglovecorebackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rozchepiy.dev.smartglovecorebackend.dto.request.CreateModelRequest;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestureModelService {
    private final GestureModelRepository gestureModelRepository;

    public GestureModel createModel(CreateModelRequest createModelRequest){
        GestureModel gestureModel = GestureModel.builder()
                .userId(createModelRequest.getUserId())
                .name(createModelRequest.getName())
                .isDefault(false)
                .status(ModelStatus.CREATED)
                .build();
        return gestureModelRepository.save(gestureModel);
    }

    public List<GestureModel> getUserModels(String userId) {
        List<GestureModel> defaultModels = gestureModelRepository.findAllByIsDefaultTrue();
        List<GestureModel> userModels = gestureModelRepository.findAllByUserId(userId);
        defaultModels.addAll(userModels);
        return defaultModels;
    }
}
