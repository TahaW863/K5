package edu.lsu.publisher.dtos;

import edu.lsu.publisher.model.HashModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * HashesDto
 * Structure of the response from the publisher
 * Hashes{
 *     FileName:{
 *     [
 *         {
 *         containerId: containerId,
 *         HashType: MD5 or SHA256,
 *         HashValue: HashValue,
 *         timestamp: timestamp,
 *         }
 *     ]
 *     }
 * }
 */
@Data
@Builder
public class HashesDto {
    private Map<String, List<HashModel>> hashes;
}
