package rozchepiy.dev.smartglovecorebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "gesture_models")
public class GestureModel {
    @Id
    private String id;

    private String userId;
    private String name;
    private boolean includesDefaultGestures;

    private boolean isDefault;

    private String s3PathToKeras;
    private String s3PathToScaler;
    private String s3PathToLabels;

    private ModelStatus status;
}