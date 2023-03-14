package edu.lsu.publisher.service;

import edu.lsu.publisher.dtos.DockerStatsSummaryDto;
import edu.lsu.publisher.model.DockerStatsModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface DockerStatsService {
    /**
     * Subscribe to the stats of a container
     * @param containerId
     * returns void
     */
    Mono<Void> subscribeStatsToContainerId(String containerId);
    /**
     * Get All the stats of a container
     * @param containerId
     * returns Mono<String>
     */
    Flux<DockerStatsModel> getLatestStatsByIdProActive(String containerId) ;
    /**
     * get All the stats of a container
     * @param containerId
     * returns Flux<DockerLogsModel>
     */

    Flux<DockerStatsModel>findAllLogsByContainerId(String containerId);

    /**
     * get the lastest stat summary of a container
     * @param containerId
     * returns Flux<DockerStatsSummaryDto>
     */
    Flux<DockerStatsSummaryDto> getLatestStatsSummaryById(String containerId);
}
