package rozchepiy.dev.smartglovecorebackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GestureSummaryDto {
    private String label;
    private long count;
}