package hei.school.course.endpoint.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PriceRest {
    private Long id;
    private Double price;
    private LocalDate dateValue;
}
