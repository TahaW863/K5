package edu.lsu.main.dtos;

import edu.lsu.main.model.ContainerModel;
import edu.lsu.main.model.VolumeModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainersDtos {
    private List<ContainerInfoDto> containerInfoDtos;
    private VolumeModel volumeModel;
}
