package edu.lsu.publisher.repository;

import edu.lsu.publisher.dtos.ContainersDtos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainersRepository extends MongoRepository<ContainersDtos, String> {
    ContainersDtos findAllBySessionId(String sessionId);
}
