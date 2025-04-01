package hei.school.course.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Criteria {
    private String key;
    private Object value;
    private String operation;
    private String conjunction;
}
