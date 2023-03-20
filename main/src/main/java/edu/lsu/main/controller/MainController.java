package edu.lsu.main.controller;

import edu.lsu.main.dtos.ContainerInfoDto;
import edu.lsu.main.dtos.ContainersDtos;
import edu.lsu.main.model.ContainerModel;
import edu.lsu.main.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class MainController {
    private final MainService mainService;

    @GetMapping("/containers/all")
    public ResponseEntity<List<ContainerModel>> getAllContainers() {
        return ResponseEntity.ok(mainService.getAllContainers());
    }
    @PostMapping("/containers/deploy")
    public ResponseEntity<String> deployContainers(@RequestBody ContainersDtos containerDtos){
        try {
            mainService.deployContainers(containerDtos.getContainerInfoDtos(), containerDtos.getVolumeModel());
            ContainersDtos filteredContainers = mainService.getFilteredContainers(containerDtos);
            filteredContainers.setSessionId(UUID.randomUUID().toString());
            mainService.saveContainers(filteredContainers);
            List<String> containerIds = filteredContainers.getContainerInfoDtos().stream().map(ContainerInfoDto::getContainerId).toList();
            mainService.informObserver(containerIds);
            return ResponseEntity.ok("Session Id: " + filteredContainers.getSessionId());
        }catch (Exception e){
            log.error("Error deploying containers: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
