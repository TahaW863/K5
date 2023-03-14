package edu.lsu.publisher.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Statistics;
import edu.lsu.publisher.dtos.DockerStatsSummaryDto;
import edu.lsu.publisher.model.DockerStatsModel;
import edu.lsu.publisher.repository.DockerStatsRepository;
import edu.lsu.publisher.service.DockerStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DockerStatsServiceImpl implements DockerStatsService {
    public static final double MB_SIZE = 1024.0 * 1024.0;
    private final DockerStatsRepository dockerStatsRepository;
    private final DockerClient dockerClient;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    @Override
    public Mono<Void> subscribeStatsToContainerId(String containerId) {
        log.info("Subscribing to container stats: {}", containerId);
        return subscribeToDockerStats(containerId)
                .then();
    }

    @Override
    public Flux<DockerStatsModel> getLatestStatsByIdProActive(String containerId) {
        log.info("Getting container pro-active stats: {}", containerId);
        Criteria criteria = Criteria.where("containerId").is(containerId);
        Query query = new Query()
                .addCriteria(criteria)
                .with(Sort.by(Sort.Direction.DESC, "$natural"))
                .limit(1);
        return reactiveMongoTemplate.find(query, DockerStatsModel.class)
                .repeat().distinctUntilChanged();
    }


    @Override
    public Flux<DockerStatsModel>findAllLogsByContainerId(String containerId) {
        log.info("Getting container all stats: {}", containerId);
        return dockerStatsRepository.findAllByContainerId(containerId)
                .thenMany(getLatestStatsByIdProActive(containerId));
    }

    @Override
    public Flux<DockerStatsSummaryDto> getLatestStatsSummaryById(String containerId) {
        log.info("Getting container stats summary: {}", containerId);
        return getLatestStatsByIdProActive(containerId)
                .map((stats) -> getStatsSummary(stats.getStats(), containerId))
                .map(Optional::get);
    }
    private Optional<DockerStatsSummaryDto> getStatsSummary(Statistics stats, String containerId) {
        try {
            ZonedDateTime preRead = ZonedDateTime.parse(stats.getPreread());
            ZonedDateTime read = ZonedDateTime.parse(stats.getRead());

            Duration duration = Duration.between(preRead, read);
            long interval = duration.toNanos();

            long cpuUsage = Objects.requireNonNull(stats.getCpuStats().getCpuUsage()).getTotalUsage();
            long preCpuUsage = Objects.requireNonNull(stats.getPreCpuStats().getCpuUsage()).getTotalUsage();
            double cpuUsagePercentage = (cpuUsage - preCpuUsage) / (double) interval * 100.0;

            double memoryUsageInMB = stats.getMemoryStats().getUsage() / MB_SIZE;

            double memoryLimitInMB = stats.getMemoryStats().getLimit() / MB_SIZE;

            long networkRxBytes = stats.getNetworks().get("eth0").getRxBytes();
            long networkTxBytes = stats.getNetworks().get("eth0").getTxBytes();
            double networkRxMB = networkRxBytes / MB_SIZE;
            double networkTxMB = networkTxBytes / MB_SIZE;

            String cpuUsagePercentageFormatted = String.format("%.2f", cpuUsagePercentage);
            String memoryUsageInMBFormatted = String.format("%.2f", memoryUsageInMB);
            String memoryLimitInMBFormatted = String.format("%.2f", memoryLimitInMB);
            String networkRxMBFormatted = String.format("%.2f", networkRxMB);
            String networkTxMBFormatted = String.format("%.2f", networkTxMB);

            return Optional.of(DockerStatsSummaryDto.builder()
                    .cpuPercent(Float.parseFloat(cpuUsagePercentageFormatted))
                    .memoryUsage(Float.parseFloat(memoryUsageInMBFormatted))
                    .memoryLimit(Float.parseFloat(memoryLimitInMBFormatted))
                    .networkI(Float.parseFloat(networkRxMBFormatted))
                    .networkO(Float.parseFloat(networkTxMBFormatted))
                    .containerId(containerId)
                    .build());
        } catch (Exception e) {
            log.error("Error while getting stats summary");
            return Optional.empty();
        }
    }


    public Flux<DockerStatsModel> subscribeToDockerStats(String containerId) {
        log.info("Subscribing to container stats: {}", containerId);
        return Flux.create(sink -> {
            dockerClient.statsCmd(containerId).exec(new ResultCallback.Adapter<>() {
                @Override
                public void onNext(Statistics stats) {
                    sink.next(DockerStatsModel.builder()
                            .containerId(containerId)
                            .stats(stats)
                            .timestamp(Date.from(Instant.now()))
                            .build());
                }
            });
        })
                .buffer(Duration.ofSeconds(1))
                .filter(l -> !l.isEmpty())
                .flatMap(dockerStatsRepository::saveAll);
    }
}
