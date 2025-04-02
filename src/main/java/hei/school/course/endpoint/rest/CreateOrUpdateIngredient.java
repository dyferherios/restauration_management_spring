package hei.school.course.endpoint.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateOrUpdateIngredient {
    private Long id;
    private String name;
}
