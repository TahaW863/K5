package edu.lsu.main;

import edu.lsu.main.service.MainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"edu.lsu.main"})
@EnableScheduling
public class MainApplication {
    static MainService mainService;
    public MainApplication(MainService mainService) {
        this.mainService = mainService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
