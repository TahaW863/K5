package edu.lsu.observer.controller;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import edu.lsu.observer.service.DockerLogsService;
import edu.lsu.observer.service.DockerStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/observer")
@Slf4j
public class ObserverController {
    private final DockerLogsService dockerLogsService;
    private final DockerStatsService dockerStatsService;
    private final DockerClient dockerClient;

    private final Map<String, List<Disposable>> subscriptions = new ConcurrentHashMap<>();

    @PostMapping(value = "/stats/{containerId}")
    public ResponseEntity<Void> subscribeStatsToContainerId(@PathVariable String containerId) {
        log.info("Subscribing to container stats: {}", containerId);
        Disposable subscription = dockerStatsService.subscribeStatsToContainerId(containerId)
                .subscribe();
        addSubscription(containerId, subscription);
        return ResponseEntity.ok().build();
    }

    private synchronized void addSubscription(String containerId, Disposable subscription) {
        if(subscriptions.containsKey(containerId)) {
            subscriptions.get(containerId).add(subscription);
        } else {
            subscriptions.put(containerId, new ArrayList<>() {{
                add(subscription);
            }});
        }
    }

    @PostMapping(value = "/logs/{containerId}")
    public ResponseEntity<Void> subscribeLogsToContainerId(@PathVariable String containerId) {
        log.info("Subscribing to container logs: {}", containerId);
        Disposable subscription = dockerLogsService.subscribeLoggerToContainerId(containerId)
                .subscribe();
        addSubscription(containerId, subscription);
        return ResponseEntity.ok().build();
    }
    @Scheduled(fixedDelay = 10000)
    public void checkContainerStatus() {
        List<Container> containers = dockerClient.listContainersCmd().exec();
        subscriptions.forEach((containerId, disposable) -> {
            if (containers.stream().noneMatch(container -> container.getId().compareTo(containerId)==0)) {
                log.info("Container {} is not running anymore, unsubscribing from stats", containerId);
                disposable.forEach(Disposable::dispose);
                subscriptions.remove(containerId);
            }
        });
    }

}
