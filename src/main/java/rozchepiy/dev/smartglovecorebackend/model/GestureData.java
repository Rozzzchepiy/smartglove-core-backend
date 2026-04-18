package rozchepiy.dev.smartglovecorebackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "gesture_data")
public class GestureData {
    @Id
    private String id;

    private String modelId;
    private String label;

    private List<List<Double>> rawData;
}
