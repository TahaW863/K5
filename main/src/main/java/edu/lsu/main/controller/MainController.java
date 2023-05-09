package edu.lsu.main.controller;

import edu.lsu.main.dtos.ContainerInfoDto;
import edu.lsu.main.dtos.ContainersDtos;
import edu.lsu.main.model.ContainerModel;
import edu.lsu.main.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MainController {
    private final MainService mainService;
    private final Queue<ContainersDtos> containersDtosQueue=new LinkedList<>();

    @GetMapping("/containers/all")
    public ResponseEntity<List<ContainerModel>> getAllContainers() {
        return ResponseEntity.ok(mainService.getAllContainers());
    }
    @PostMapping("/containers/deploy")
    public ResponseEntity<String> deployContainers(@RequestBody ContainersDtos containerDtos){
        try {
            ContainersDtos filteredContainers = deployContainerDtos(containerDtos);
            return ResponseEntity.ok("Session Id: " + filteredContainers.getSessionId());
        }catch (Exception e){
            log.error("Error deploying containers: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ContainersDtos deployContainerDtos(ContainersDtos containerDtos) {
        mainService.deployContainers(containerDtos.getContainerInfoDtos(), containerDtos.getVolumeModel());
        ContainersDtos filteredContainers = mainService.getFilteredContainers(containerDtos);
        if(Objects.isNull(filteredContainers.getSessionId()))
            filteredContainers.setSessionId(UUID.randomUUID().toString());
        mainService.saveContainers(filteredContainers);
        List<String> containerIds = filteredContainers.getContainerInfoDtos().stream().map(ContainerInfoDto::getContainerId).toList();
        mainService.informObserver(containerIds);
        return filteredContainers;
    }

    @PostMapping("/containers/deploy/batchsize/{batchSize}")
    public ResponseEntity<String> deployContainers(@RequestBody ContainersDtos containerDtos, @PathVariable int batchSize){
        try {
            int getContainerInfoDtosSize = containerDtos.getContainerInfoDtos().size();
            int numberOfBatches = getContainerInfoDtosSize / batchSize;
            int remainder = getContainerInfoDtosSize % batchSize;
            int start = 0;
            int end = batchSize;
            // make session id for all the containers to be the same so that they can be removed together
            containerDtos.setSessionId(UUID.randomUUID().toString());
            for (int i = 0; i < numberOfBatches; i++) {
                ContainersDtos containersDtosLocal = new ContainersDtos();
                containersDtosLocal.setContainerInfoDtos(containerDtos.getContainerInfoDtos().subList(start, end));
                containersDtosLocal.setVolumeModel(containerDtos.getVolumeModel());
                containersDtosLocal.setSessionId(containerDtos.getSessionId());
                containersDtosQueue.add(containersDtosLocal);
                start = end;
                end += batchSize;
            }
            if(remainder > 0){
                ContainersDtos containersDtos = new ContainersDtos();
                containersDtos.setContainerInfoDtos(containerDtos.getContainerInfoDtos().subList(start, getContainerInfoDtosSize));
                containersDtos.setVolumeModel(containerDtos.getVolumeModel());
                containersDtos.setSessionId(containerDtos.getSessionId());
                containersDtosQueue.add(containersDtos);
            }
            return ResponseEntity.ok("Session Id: " + containerDtos.getSessionId() + " Number of batches: " + numberOfBatches + " Remainder: " + remainder + " Queue size: " + containersDtosQueue.size());
        }catch (Exception e){
            log.error("Error deploying containers: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Scheduled(fixedDelay = 5000)
    public void deployContainers (){
        if(!containersDtosQueue.isEmpty()){
            ContainersDtos containersDtos = containersDtosQueue.poll();
            deployContainerDtos(containersDtos);
        }
    }
}
