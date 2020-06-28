package com.example.clouddisk.dto;


import com.example.clouddisk.models.Access;
import com.example.clouddisk.models.CloudFile;
import lombok.Data;

@Data
public class CloudFileDto {

    private Long id;
    private String name;
    private String type;

    private String avatar;
    private String access_link;
    private String path;
    private Long size = 0L;

    private Access access;

    private Long parentId;
    private CloudFileDto parent;


    public static CloudFile toCloudFile(CloudFileDto cloudFileDto) {
        CloudFile cloudFile = new CloudFile();
        cloudFile.setName(cloudFileDto.getName());
        cloudFile.setType(cloudFileDto.getType());
        cloudFile.setAvatar(cloudFileDto.getAvatar());
        cloudFile.setAccess_link(cloudFileDto.getAccess_link());
        cloudFile.setAccess(cloudFileDto.getAccess());
        cloudFile.setSize(cloudFileDto.getSize());

        return cloudFile;
    }

    public static CloudFileDto fromCloudFile(CloudFile cloudFile) {

        CloudFileDto cloudFileDto = new CloudFileDto();
        cloudFileDto.setName(cloudFile.getName());
        cloudFileDto.setId(cloudFile.getId());
        cloudFileDto.setType(cloudFile.getType());
        cloudFileDto.setAccess(cloudFile.getAccess());
        cloudFileDto.setAccess_link(cloudFile.getAccess_link());
        cloudFileDto.setAvatar(cloudFile.getAvatar());
        cloudFileDto.setSize(cloudFile.getSize());
        if (cloudFile.getParent() != null) {
            cloudFileDto.setParentId(cloudFile.getParent().getId());
            cloudFileDto.setParent(CloudFileDto.fromCloudFile(cloudFile.getParent()));
        }
        return cloudFileDto;
    }
}
