package rozchepiy.dev.smartglovecorebackend.dto.message;

import lombok.Data;

@Data
public class TrainResultMessage {
    private String modelId;
    private String status;
    private String s3KerasPath;
    private String s3ScalerPath;
    private String s3LabelsPath;
    private String errorMessage;
}
