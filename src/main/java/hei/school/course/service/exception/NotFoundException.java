package hei.school.course.service.exception;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotFoundException extends RuntimeException {
    public NotFoundException(Exception e) {
        super(e);
    }
    public NotFoundException(String message) {
        super(message);
    }
}
