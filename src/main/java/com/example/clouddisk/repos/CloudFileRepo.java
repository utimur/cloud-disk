package com.example.clouddisk.repos;

import com.example.clouddisk.models.CloudFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CloudFileRepo extends CrudRepository<CloudFile, Long> {

    List<CloudFile> findCloudFilesByParentIdAndDiskId(Long parentId, Long diskId);
}
