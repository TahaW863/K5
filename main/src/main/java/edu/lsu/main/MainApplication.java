package edu.lsu.main;

import edu.lsu.main.service.MainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class MainApplication {
    static MainService mainService;
    public MainApplication(MainService mainService) {
        this.mainService = mainService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        //mainService.removeAllContainersWithCommand("/app/memory_dumps");
    }

}
