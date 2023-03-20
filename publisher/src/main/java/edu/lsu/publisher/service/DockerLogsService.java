package edu.lsu.publisher.service;

import edu.lsu.publisher.dtos.HashesDto;
import edu.lsu.publisher.model.DockerLogsModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
public interface DockerLogsService {
    /**
     * Subscribe to the logs of a container
     * @param containerId
     * returns void
     */
    Mono<Void> subscribeLoggerToContainerId(String containerId);
    /**
     * Get All the logs of a container
     * @param containerId
     * returns Mono<String>
     */
    Flux<DockerLogsModel> getLogs(String containerId) throws IOException;
    /**
     * get changes in the logs of a container
     * @param containerId
     * returns Flux<DockerLogsModel>
     */
    Flux<DockerLogsModel> getAllLogsFromAllContainers(String containerId) throws IOException;

    /**
     * get the latest logs of a container
     * @param containerId
     * returns Flux<DockerLogsModel>
     */
    Flux<DockerLogsModel> getLatestLogsByIdProActive(String containerId) ;
    /**
     * get the latest logs of a container
     * @param containerId
     * returns Flux<DockerLogsModel>
     */

    Flux<DockerLogsModel> getLatestLogsByIdOnLastResultAvailable(String containerId) ;
    /**
     * get the latest logs of a container
     * @param containerId
     * returns Flux<DockerLogsModel>
     */

    Flux<DockerLogsModel> findAllLogsByContainerId(String containerId);

    /**
     * remove duplicate logs
     * @return void
     */

    void removeDuplicateLogs();
    /**
     * get all the logs hashes
     */
    HashesDto getAllLogsByContainerIds();

    HashesDto getAllLogsByContainerIds(String sessionId);
}
