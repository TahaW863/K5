package edu.lsu.observer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Statistics;
import edu.lsu.observer.model.DockerStatsModel;
import edu.lsu.observer.service.DockerLogsService;
import edu.lsu.observer.service.DockerStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.math.BigInteger;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableScheduling
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ObserverApplication {
    static DockerClient dockerClient;
    static DockerLogsService dockerLogsService;
    static DockerStatsService dockerStatsService;
    public ObserverApplication(DockerClient dockerClient
            , DockerLogsService dockerLogsService,
                               DockerStatsService dockerStatsService){
        this.dockerClient = dockerClient;
        this.dockerLogsService = dockerLogsService;
        this.dockerStatsService = dockerStatsService;
    }
    public static void main(String[] args) {

        SpringApplication.run(ObserverApplication.class, args);

        log.info("Observer Application Started");
        /*log.info("Docker events are being monitored");
        dockerClient.eventsCmd().exec(new ResultCallback.Adapter<Event>() {
            @Override
            public void onNext(Event event) {
                log.info("Docker event: {}", event);
            }
        });*/

        /*log.info("All docker containers are being monitored");
        dockerClient.listContainersCmd().exec().forEach(container -> {
            log.info("Container: {}", container);
        });
        log.info("All docker containers stats are being monitored");
        dockerClient.listContainersCmd().exec().forEach(container -> {
            log.info("Container: {}", container);
            dockerClient.statsCmd(container.getId()).exec(new ResultCallback.Adapter<>() {
                @Override
                public void onNext(com.github.dockerjava.api.model.Statistics stats) {
                    log.info("Container stats: {}", stats);
                }
            });
        });*/
        /*log.info("All docker containers logs are being monitored");
        dockerClient.listContainersCmd().exec().forEach(container -> {
            log.info("Container: {}", container);
            dockerClient.logContainerCmd(container.getId()).withStdOut(true).withStdErr(true).withFollowStream(true).exec(new ResultCallback.Adapter<>() {
                @Override
                public void onNext(Frame frame) {
                    log.info("Container logs: {}", frame);

                }
            });
        });*/
        //try {
         //   dockerLogsService.subscribeLoggerToContainerId("95fe3e7980d81ca833f57a147058b57fe2ebe8730526ac967caecfb40b0ee205").subscribe();
            /*dockerLogsService.getLatestLogsByIdProActive("95fe3e7980d81ca833f57a147058b57fe2ebe8730526ac967caecfb40b0ee205").subscribe(logs -> {
                log.info("Container logs: {}", logs.getLogs());
            });*/
            /*dockerLogsService.getLatestLogsByIdOnLastResultAvailable("95fe3e7980d81ca833f57a147058b57fe2ebe8730526ac967caecfb40b0ee205").subscribe(logs -> {
                log.info("Container logs: {}", logs.getLogs());
            });*/

            /*dockerLogsService.findAllLogsByContainerId("95fe3e7980d81ca833f57a147058b57fe2ebe8730526ac967caecfb40b0ee205").subscribe(logs -> {
                log.info("Container logs: {}", logs.getLogs());
            });*/
            //dockerLogsService.removeDuplicateLogs();
       // }catch (Exception e){
       //     log.error("Error: {}", e.getMessage());
       // }
        /*try {
            dockerStatsService.subscribeStatsToContainerId("c92a85ad03e9").subscribe();
            dockerStatsService.getLatestStatsByIdProActive("c92a85ad03e9").subscribe(statsModel -> {
                try {
                    printSummary(statsModel);
                }catch (Exception e){
                    log.error("Error: {}", e.getMessage());
                }
            });
        }catch (Exception e){
            log.error("Error: {}", e.getMessage());
        }*/
        /*dockerStatsService.getLatestStatsSummaryById("c92a85ad03e9")
                .subscribe(statsModel -> {
                    log.info("Container stats: {}", statsModel);
                });*/
    }

    /*private static void printSummary(DockerStatsModel statsModel) {
        Statistics stats = statsModel.getStats();
        ZonedDateTime preRead = ZonedDateTime.parse(stats.getPreread());
        ZonedDateTime read = ZonedDateTime.parse(stats.getRead());

        Duration duration = Duration.between(preRead, read);
        long interval = duration.toNanos();

        long cpuUsage = Objects.requireNonNull(stats.getCpuStats().getCpuUsage()).getTotalUsage();
        long preCpuUsage = Objects.requireNonNull(stats.getPreCpuStats().getCpuUsage()).getTotalUsage();
        double cpuUsagePercentage = (cpuUsage - preCpuUsage) / (double) interval * 100.0;

        double memoryUsageInMB = stats.getMemoryStats().getUsage() / (1024.0 * 1024.0);
        double memoryLimitInMB = stats.getMemoryStats().getLimit() / (1024.0 * 1024.0);

        long networkRxBytes = stats.getNetworks().get("eth0").getRxBytes();
        long networkTxBytes = stats.getNetworks().get("eth0").getTxBytes();
        double networkRxMB = networkRxBytes / (1024.0 * 1024.0);
        double networkTxMB = networkTxBytes / (1024.0 * 1024.0);

        String cpuUsagePercentageFormatted = String.format("%.2f", cpuUsagePercentage);
        String memoryUsageInMBFormatted = String.format("%.2f", memoryUsageInMB);
        String memoryLimitInMBFormatted = String.format("%.2f", memoryLimitInMB);
        String networkRxMBFormatted = String.format("%.2f", networkRxMB);
        String networkTxMBFormatted = String.format("%.2f", networkTxMB);

        log.info("CPU: {}% Memory: {}MB / {}MB Network: {}MB / {}MB",
                cpuUsagePercentageFormatted,
                memoryUsageInMBFormatted,
                memoryLimitInMBFormatted,
                networkRxMBFormatted,
                networkTxMBFormatted);
    }*/

}
