package rozchepiy.dev.smartglovecorebackend.dto.message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainTaskMessage {
    private String taskId;
    private String modelId;
    private String userId;
}
