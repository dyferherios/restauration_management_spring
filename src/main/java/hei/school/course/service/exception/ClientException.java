package hei.school.course.service.exception;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ClientException extends RuntimeException {
    public ClientException(Exception e) {
        super(e);
    }
    public ClientException(String message) {
        super(message);
    }
}
