package edu.lsu.main.repository;

import edu.lsu.main.dtos.ContainersDtos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainersRepository extends MongoRepository<ContainersDtos, String> {
}
