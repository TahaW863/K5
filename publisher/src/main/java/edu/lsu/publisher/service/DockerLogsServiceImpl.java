package edu.lsu.publisher.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import edu.lsu.publisher.model.DockerLogsModel;
import edu.lsu.publisher.repository.DockerLogsRepository;
import edu.lsu.publisher.service.DockerLogsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Component
@RequiredArgsConstructor
@Slf4j
public class DockerLogsServiceImpl implements DockerLogsService {
    private final DockerLogsRepository dockerLogsRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final DockerClient dockerClient;

    @Override
    public Mono<Void> subscribeLoggerToContainerId(String containerId){
        log.info("Subscribing to container logs: {}", containerId);
        return subscribeCycleWrapperLogs(containerId);
    }


    @Override
    public Flux<DockerLogsModel> getLogs(String containerId) throws IOException {
        log.info("Getting container logs: {}", containerId);
        return dockerLogsRepository.findAllByContainerId(containerId);
    }

    @Override
    public Flux<DockerLogsModel> getLatestLogsByIdProActive(String containerId) {
        Criteria criteria = Criteria.where("containerId").is(containerId);
        Query query = new Query()
                .addCriteria(criteria)
                .with(Sort.by(Sort.Direction.DESC, "$natural"))
                .limit(1);

        return reactiveMongoTemplate.find(query, DockerLogsModel.class)
                .repeat().distinctUntilChanged();
    }
    @Override
    public Flux<DockerLogsModel> getLatestLogsByIdOnLastResultAvailable(String containerId) {
        Criteria criteria = Criteria.where("containerId").is(containerId);
        return Flux.interval(Duration.ofMillis(100))
                .flatMap(i -> reactiveMongoTemplate.find(
                        Query.query(criteria)
                                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                                .limit(1),
                        DockerLogsModel.class
                ))
                .distinctUntilChanged();
    }

    @Override
    public Flux<DockerLogsModel> findAllLogsByContainerId(String containerId) {
        return dockerLogsRepository.findAllByContainerId(containerId).thenMany(getLatestLogsByIdProActive(containerId));
    }


    @Override
    public Flux<DockerLogsModel> getAllLogsFromAllContainers(String containerId) throws IOException {
        log.info("Getting container logs changes: {}", containerId);
        return dockerLogsRepository.findAll();
    }

    private Mono<Void> subscribeCycleWrapperLogs(String containerId) {
        return subscribeToDockerContainerLogsViaClient(containerId)
                .retry(10)
                .then();
    }


    private Flux<DockerLogsModel> subscribeToDockerContainerLogsViaClient(String containerId) {
        log.info("Subscribing to container logs via client: {}", containerId);
        return Flux.create(emitter -> {
                    dockerClient.logContainerCmd(containerId)
                            .withStdOut(true)
                            .withStdErr(true)
                            .withFollowStream(true)
                            .exec(new ResultCallback.Adapter<>() {
                                @Override
                                public void onNext(Frame frame) {
                                    String logs = new String(frame.getPayload(), StandardCharsets.UTF_8);
                                    DockerLogsModel log = DockerLogsModel.builder()
                                            .containerId(containerId)
                                            .logs(logs)
                                            .timestamp(Date.from(Instant.now()))
                                            .build();
                                    emitter.next(log);
                                }

                                @Override
                                public void onComplete() {
                                    emitter.complete();
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    emitter.error(throwable);
                                }
                            });
                })
                .buffer(Duration.ofSeconds(1))
                .filter(list -> !list.isEmpty())
                .flatMap(dockerLogsRepository::saveAll);
    }
    public void removeDuplicateLogs(){
        List<String> uniqueLogs = reactiveMongoTemplate.aggregate(newAggregation(match(Criteria.where("containerId").is("containerId"))), DockerLogsModel.class, DockerLogsModel.class)
                .toStream()
                .map(DockerLogsModel::getLogs)
                .distinct()
                .toList();
        for (String uniqueLog : uniqueLogs) {
            Query query = new Query();
            query.addCriteria(Criteria.where("logs").is(uniqueLog));
            query.with(Sort.by(Sort.Direction.DESC, "timestamp")).skip(1);
            List<DockerLogsModel> duplicates = reactiveMongoTemplate.find(query, DockerLogsModel.class).collectList().block();
            if (duplicates != null) {
                for (DockerLogsModel duplicate : duplicates) {
                    reactiveMongoTemplate.remove(duplicate).block();
                }
            }
        }
    }

}
