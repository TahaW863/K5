package edu.lsu.main.controller;

import edu.lsu.main.dtos.ContainersDtos;
import edu.lsu.main.model.ContainerModel;
import edu.lsu.main.model.VolumeModel;
import edu.lsu.main.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> deployContainers(@RequestBody ContainersDtos containerDtos){
        mainService.deployContainers(containerDtos.getContainerInfoDtos(), containerDtos.getVolumeModel());
        List<String> containerIds = mainService.getContainerIds(containerDtos);
        mainService.informObserver(containerIds);
        return ResponseEntity.ok().build();
    }
}
