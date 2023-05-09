package edu.lsu.publisher.controller;


import edu.lsu.publisher.dtos.DockerStatsSummaryDto;
import edu.lsu.publisher.dtos.HashesDto;
import edu.lsu.publisher.model.DockerLogsModel;
import edu.lsu.publisher.service.DockerLogsService;
import edu.lsu.publisher.service.DockerStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

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
    /*@GetMapping(value = "/stats/summary/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DockerStatsSummaryDto>> getLatestStatsSummaryById(@PathVariable String sessionId) {
        log.info("Getting container stats summary: {}", sessionId);
        return ResponseEntity.ok(dockerStatsService.getLatestStatsSummaryBySessionId(sessionId));
    }*/
    @GetMapping(value = "/logs/{containerId}/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<DockerLogsModel>> getLogsById(@PathVariable String containerId) {
        log.info("Getting container logs: {}", containerId);
        return ResponseEntity.ok(dockerLogsService.findAllLogsByContainerId(containerId));
    }

    @GetMapping(value = "/logs/hashes/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashesDto> getLogsHashes() {
        log.info("Getting container logs hashes");
        return ResponseEntity.ok(dockerLogsService.getAllLogsByContainerIds());
    }
    @GetMapping(value = "/logs/hashes/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashesDto> getLogsHashes(@PathVariable String sessionId) {
        log.info("Getting container logs hashes");
        return ResponseEntity.ok(dockerLogsService.getAllLogsByContainerIds(sessionId));
    }
}
