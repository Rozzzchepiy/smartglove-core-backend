package rozchepiy.dev.smartglovecorebackend.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PredictRequest {
    private String modelId;
    private List<List<Double>> rawData;
}