package rozchepiy.dev.smartglovecorebackend.dto.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiInitRequest {
    private String modelId;
    private String modelUrl;
    private String scalerUrl;
    private String labelsUrl;
}