package edu.lsu.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VolumeModel {

    private String absolutePathOnHost;
    private String mountPathInContainer="/app";
}
