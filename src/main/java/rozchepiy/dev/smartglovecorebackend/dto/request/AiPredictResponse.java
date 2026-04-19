package rozchepiy.dev.smartglovecorebackend.dto.external;

import lombok.Data;

@Data
public class AiPredictResponse {
    private String predictedLabel;
    private Double confidence;
}