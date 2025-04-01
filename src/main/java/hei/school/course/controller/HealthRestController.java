package hei.school.course.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthRestController {
    @GetMapping("/ping")
    public ResponseEntity<String> healthCheck() {
        try{
            return ResponseEntity.ok("pong");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
