package edu.lsu.main.service;

import edu.lsu.main.dtos.ContainerInfoDto;
import edu.lsu.main.dtos.ContainersDtos;
import edu.lsu.main.model.ContainerModel;
import edu.lsu.main.model.VolumeModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MainService {
    /**
     * Deploy Containers
     * @param List<ContainerModel>
     * List of containers to deploy
     * returns void
     */
    void deployContainers(List<ContainerInfoDto> containerInfoDtos, VolumeModel volumeModel);
    /**
     * Get All the containers
     * returns List<ContainerModel>
     */
    List<ContainerModel> getAllContainers();

    void informObserver(List<String> containerIds);

    ContainersDtos getFilteredContainers(ContainersDtos containerDtos);
    void removeAllContainersWithCommand(String command);

    void saveContainers(ContainersDtos filteredContainers);
}
