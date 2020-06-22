package com.example.clouddisk.repos;

import com.example.clouddisk.models.Disk;
import org.springframework.data.repository.CrudRepository;

public interface DiskRepo extends CrudRepository<Disk, Long> {
}
