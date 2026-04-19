package rozchepiy.dev.smartglovecorebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateModelRequest {
    @NotBlank(message = "Назва моделі не може бути порожньою")
    private String name;

}
