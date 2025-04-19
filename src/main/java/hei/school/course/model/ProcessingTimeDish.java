package hei.school.course.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
public class ProcessingTimeDish {
    private Long dishId;
    private String dishName;
    private String durationFormat;
    private String durationLevel;
    private Double processingTime;
    private String startDate;
    private String endDate;
}
