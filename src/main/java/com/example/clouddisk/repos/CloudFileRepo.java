package com.example.clouddisk.repos;

import com.example.clouddisk.models.CloudFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CloudFileRepo extends CrudRepository<CloudFile, Long> {

    List<CloudFile> findCloudFilesByParentIdAndDiskId(Long parentId, Long diskId);

    CloudFile findByParentIdAndNameAndDiskId(Long parendId, String name, Long diskId);

    List<CloudFile> findCloudFilesByParentIdAndDiskIdOrderByName(Long parentId, Long diskId);
    List<CloudFile> findCloudFilesByParentIdAndDiskIdOrderByNameDesc(Long parentId, Long diskId);

    List<CloudFile> findCloudFilesByParentIdAndDiskIdOrderByType(Long parentId, Long diskId);
    List<CloudFile> findCloudFilesByParentIdAndDiskIdOrderByTypeDesc(Long parentId, Long diskId);

    List<CloudFile> findCloudFilesByDiskId(Long diskId);
    List<CloudFile> findCloudFilesByParentIdAndDiskIdOrderByCreatedAt(Long parentId, Long diskId);
    List<CloudFile> findCloudFilesByParentIdAndDiskIdOrderByCreatedAtDesc(Long parentId, Long diskId);

}
