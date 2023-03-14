package edu.lsu.main.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import edu.lsu.main.dtos.ContainerInfoDto;
import edu.lsu.main.dtos.ContainersDtos;
import edu.lsu.main.model.ContainerModel;
import edu.lsu.main.model.VolumeModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainServiceImpl implements MainService{
    private final DockerClient dockerClient;
    private final RestTemplate restTemplate;
    @Override
    public void deployContainers(List<ContainerInfoDto> containerInfoDtos, VolumeModel volumeModel) {
        log.info("Deploying containers");
        // make sure to tell observer to start listening to logs, stats, events
        log.info("Creating containers {}", containerInfoDtos);
        List<String> containerIds = new ArrayList<>();
        containerInfoDtos.forEach(containerModel -> {
            containerIds.add(dockerClient.createContainerCmd(containerModel.getImageNameWithTag())
                    .withCmd(containerModel.getCommand().split(" "))
                    .withHostConfig(HostConfig.newHostConfig().withBinds(new Bind(volumeModel.getAbsolutePathOnHost(), new Volume(volumeModel.getMountPathInContainer()))))
                    .exec().getId());
        });
        log.info("Starting containers {}", containerIds);
        containerIds.forEach(containerId -> {
            dockerClient.startContainerCmd(containerId).exec();
        });
    }

    @Override
    public List<ContainerModel> getAllContainers() {
        log.info("Getting all containers");
        List<ContainerModel> containerModels = new ArrayList<>();
        List<Container> containers = dockerClient.listContainersCmd().exec();
        containers.forEach(container -> {
            ContainerModel containerModel = ContainerModel.builder()
                    .container(container)
                    .build();
            containerModels.add(containerModel);
        });
        return containerModels;
    }

    @Override
    public void informObserver(List<String> containerIds) {
        log.info("Informing observer about containers {}", containerIds);
        containerIds.forEach(containerId -> {
            restTemplate.postForEntity("http://localhost:7070/api/observer/stats/"+containerId, null, Void.class);
            //restTemplate.postForEntity("http://localhost:7070/api/observer/logs/"+containerId, null, Void.class);
        });
    }

    @Override
    public List<String> getContainerIds(ContainersDtos containerDtos) {
        List<String> containerIds = new ArrayList<>();
        List<Container> containers = dockerClient.listContainersCmd().exec();
        containers.forEach(container -> {
            containerDtos.getContainerInfoDtos().forEach(containerInfoDto -> {
                if(container.getCommand().compareTo(containerInfoDto.getCommand())==0){
                    containerIds.add(container.getId());
                    log.info("Container id: {}, command: {}", container.getId(), container.getCommand());
                }
            });
        });
        return containerIds;
    }
    public void removeAllContainersWithCommand(String command){
        List<Container> containers = dockerClient.listContainersCmd().exec();
        containers.forEach(container -> {
            if(container.getCommand().contains(command)){
                dockerClient.stopContainerCmd(container.getId()).exec();
                dockerClient.removeContainerCmd(container.getId()).exec();
            }
        });
    }
}
