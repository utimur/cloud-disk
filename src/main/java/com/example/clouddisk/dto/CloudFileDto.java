package com.example.clouddisk.dto;


import com.example.clouddisk.models.Access;
import com.example.clouddisk.models.CloudFile;
import lombok.Data;

import java.util.Date;

@Data
public class CloudFileDto {

    private Long id;
    private String name;
    private String type;

    private String avatar;
    private String accessLink;
    private String path;
    private Long size = 0L;
    private Boolean isFavourite;

    private Access access;

    private Long parentId;
    private CloudFileDto parent;
    private Date createdAt;


    public static CloudFile toCloudFile(CloudFileDto cloudFileDto) {
        CloudFile cloudFile = new CloudFile();
        cloudFile.setName(cloudFileDto.getName());
        cloudFile.setType(cloudFileDto.getType());
        cloudFile.setAvatar(cloudFileDto.getAvatar());
        cloudFile.setAccessLink(cloudFileDto.getAccessLink());
        cloudFile.setAccess(cloudFileDto.getAccess());
        cloudFile.setSize(cloudFileDto.getSize());
        cloudFile.setCreatedAt(cloudFileDto.getCreatedAt());

        return cloudFile;
    }

    public static CloudFileDto fromCloudFile(CloudFile cloudFile) {

        CloudFileDto cloudFileDto = new CloudFileDto();
        cloudFileDto.setName(cloudFile.getName());
        cloudFileDto.setId(cloudFile.getId());
        cloudFileDto.setType(cloudFile.getType());
        cloudFileDto.setAccess(cloudFile.getAccess());
        cloudFileDto.setAccessLink(cloudFile.getAccessLink());
        cloudFileDto.setAvatar(cloudFile.getAvatar());
        cloudFileDto.setSize(cloudFile.getSize());
        cloudFileDto.setIsFavourite(cloudFile.getIsFavourite());
        if (cloudFile.getParent() != null) {
            cloudFileDto.setParentId(cloudFile.getParent().getId());
            cloudFileDto.setParent(CloudFileDto.fromCloudFile(cloudFile.getParent()));
        }
        cloudFileDto.setCreatedAt(cloudFile.getCreatedAt());
        return cloudFileDto;
    }
}
