package edu.lsu.observer.repository;

import edu.lsu.observer.model.DockerStatsModel;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository

public interface DockerStatsRepository extends ReactiveMongoRepository<DockerStatsModel, String> {
    Publisher<? extends DockerStatsModel> saveAll(List<Object> objects);

    Flux<DockerStatsModel> findAllByContainerId(String containerId);
}
