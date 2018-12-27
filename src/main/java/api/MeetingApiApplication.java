package api;

import api.utils.TimeUtils;
import org.jsets.shiro.config.EnableJsetsShiro;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.xml.crypto.Data;
import java.util.Date;

@EnableJsetsShiro
@SpringBootApplication
public class MeetingApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeetingApiApplication.class, args);
//        System.out.println(TimeUtils.getDataE(new Date().getTime()+3*60*1000*60*24));
//        System.out.println(TimeUtils.getTodayZeroPointTimestamps());
//        System.out.println(TimeUtils.getOneDayTimestamps(new Date().getTime()));
//        System.out.println(TimeUtils.getOneDayZeroPointTimestamps(new Date().getTime()));
    }
}

