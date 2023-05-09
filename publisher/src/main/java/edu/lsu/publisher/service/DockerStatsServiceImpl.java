package edu.lsu.publisher.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Statistics;
import edu.lsu.publisher.dtos.DockerStatsSummaryDto;
import edu.lsu.publisher.model.DockerStatsModel;
import edu.lsu.publisher.repository.DockerStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
@Slf4j
public class DockerStatsServiceImpl implements DockerStatsService {
    public static final double MB_SIZE = 1024.0 * 1024.0;
    private final DockerStatsRepository dockerStatsRepository;
    private final DockerClient dockerClient;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final RestTemplate restTemplate;
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
                .map((stats) -> {
                            return getStatsSummary(stats.getStats(), containerId, stats.getTimestamp(), "", stats.getId());
                })
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
    private String getName(String containerId) {
        String[] python3Containers = {"08aa2bd5e551cc7d22", "07eb06165","ce4794d4ae86","67d1713042d8","e6a734dad3a7","bef3764634c5","8cb5d77c6fa9","82c71f006ddd","d2eca981755f","30e6a7c50640","6d67e6f1d157","e43b55dd5e5b","92b4a1e017a4","2729d9c595c2"};
        String[] pystonContainers = {
                "f6e39e8a5dc9",
                "9e8dedaaa154",
                "53f6391da2fe",
                "9257bf6d9ddf",
                "317e82980d59",
                "6b07a5bfa841",
                "b92a1c8e62f8",
                "4da710e078b2",
                "568aaeaccdf5",
                "3e845c731ef2",
                "c66bc5e0b473",
                "08f618574f2d",
                "f555fba1592d",
                "6e72e7663ab0"};
        String[] pyjionContainers= {"bac1347a4a80",
                "a8b3dfd14e82",
                "e95b3410fd6e",
                "f75b31e241d1",
                "d27c6ef8828b",
                "a8027754a332",
                "a8dbfa258e5b",
                "cac6cf9b66f8",
                "19945772f15e",
                "260449482b59",
                "9ce449f40627",
                "8db8c44398fd",
                "0d22a268cbd1",
                "3397dde42060"};
        String[] pypy3Containers = {"48431b41de9b",
                "180ded962eec",
                "f5d87df924ff",
                "459b19c3d569",
                "d269f5bd75e0",
                "73fb5361eadd",
                "e2156fd51ff4",
                "7f7d6a1e5baf",
                "49f56cb8901a",
                "02ed79119705",
                "6eb18991c94b",
                "804ec6331056",
                "974cffd146ec",
                "6c92ea4ac65e"};
        AtomicReference<String> nameTOReturn = new AtomicReference<>("");
        Arrays.stream(python3Containers).forEach((id) -> {
            if (containerId.contains(id)) {
                nameTOReturn.set("python3");
            }
        });
        Arrays.stream(pystonContainers).forEach((id) -> {
            if (containerId.contains(id)) {
                nameTOReturn.set("pyston");
            }
        });
        Arrays.stream(pyjionContainers).forEach((id) -> {
            if (containerId.contains(id)) {
                nameTOReturn.set("pyjion");
            }
        });
        Arrays.stream(pypy3Containers).forEach((id) -> {
            if (containerId.contains(id)) {
                nameTOReturn.set("pypy3");
            }
        });
        return nameTOReturn.get();
    }
    @Override
    public Flux<DockerStatsSummaryDto> getAllStatsSummaryForAllContainers() {
        log.info("Getting all container stats summary");
        return dockerStatsRepository.findAllByOrderByIdAsc()
                .map((stats) -> getStatsSummary(stats.getStats(), stats.getContainerId(), stats.getTimestamp(), getName(stats.getContainerId()), stats.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Optional<DockerStatsSummaryDto> getStatsSummary(Statistics stats, String containerId, Date timestamp, String name, String id) {
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
                    .timestamp(timestamp)
                    .name(name)
                    .id(id)
                    .build());
        } catch (Exception e) {
            log.error("Error while getting stats summary");
        }
        return Optional.empty();
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
