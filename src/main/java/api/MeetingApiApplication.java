package api;

import org.jsets.shiro.config.EnableJsetsShiro;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableJsetsShiro
@SpringBootApplication
public class MeetingApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeetingApiApplication.class, args);
    }
}

