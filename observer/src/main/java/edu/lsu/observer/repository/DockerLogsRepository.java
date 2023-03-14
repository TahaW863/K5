package edu.lsu.observer.repository;

import edu.lsu.observer.model.DockerLogsModel;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Repository
public interface DockerLogsRepository extends ReactiveMongoRepository<DockerLogsModel, String> {
    Mono<DockerLogsModel> existsDockerLogsModelByContainerId(String containerId);
    Flux<DockerLogsModel> findAllByContainerId(String containerId);

    Publisher<? extends DockerLogsModel> insert(List<Object> logs);

    Mono<Boolean> existsByLogs(String logs);

    Publisher<? extends DockerLogsModel> saveAll(List<Object> objects);
}
