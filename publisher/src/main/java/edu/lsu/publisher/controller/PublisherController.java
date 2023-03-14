package edu.lsu.publisher.controller;


import edu.lsu.publisher.dtos.DockerStatsSummaryDto;
import edu.lsu.publisher.model.DockerLogsModel;
import edu.lsu.publisher.service.DockerLogsService;
import edu.lsu.publisher.service.DockerStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/publisher")
@Slf4j
public class PublisherController {
    private final DockerStatsService dockerStatsService;
    private final DockerLogsService dockerLogsService;

    @GetMapping(value = "/stats/summary/{containerId}/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<DockerStatsSummaryDto>> getLatestStatsSummaryById(@PathVariable String containerId) {
        log.info("Getting container stats summary: {}", containerId);
        return ResponseEntity.ok(dockerStatsService.getLatestStatsSummaryById(containerId));
    }
    @GetMapping(value = "/logs/{containerId}/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<DockerLogsModel>> getLogsById(@PathVariable String containerId) {
        log.info("Getting container logs: {}", containerId);
        return ResponseEntity.ok(dockerLogsService.findAllLogsByContainerId(containerId));
    }

}
