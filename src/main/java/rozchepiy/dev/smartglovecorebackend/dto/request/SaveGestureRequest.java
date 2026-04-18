package rozchepiy.dev.smartglovecorebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SaveGestureRequest {

    @NotBlank(message = "Назва жесту (label) є обов'язковою")
    private String label;

    @NotEmpty(message = "Масив даних не може бути порожнім")
    private List<List<Double>> rawData;
}
