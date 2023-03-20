package edu.lsu.publisher.dtos;


import edu.lsu.publisher.model.VolumeModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ContainersDtos {
    @Id
    private String id;
    private List<ContainerInfoDto> containerInfoDtos;
    private VolumeModel volumeModel;
    private String sessionId;
}
