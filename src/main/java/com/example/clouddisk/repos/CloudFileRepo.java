package com.example.clouddisk.repos;

import com.example.clouddisk.models.CloudFile;
import org.springframework.data.repository.CrudRepository;

public interface CloudFileRepo extends CrudRepository<CloudFile, Long> {
}
