package com.example.clouddisk.service.file;

import com.example.clouddisk.dto.CloudFileDto;
import com.example.clouddisk.exceptions.file.DirAlreadyExistException;
import com.example.clouddisk.exceptions.file.DirNotCreatedException;
import com.example.clouddisk.models.Basket;
import com.example.clouddisk.models.CloudFile;
import com.example.clouddisk.models.Disk;
import com.example.clouddisk.models.User;
import com.example.clouddisk.repos.BasketRepo;
import com.example.clouddisk.repos.CloudFileRepo;
import com.example.clouddisk.repos.DiskRepo;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileService {

    public static String FILE_PATH = "src\\main\\resources\\static\\";
    public static String DEFAULT_AVATAR = "src\\main\\resources\\avatar.jpg";

    private final CloudFileRepo cloudFileRepo;
    private final DiskRepo diskRepo;
    private final BasketRepo basketRepo;

    public FileService(CloudFileRepo cloudFileRepo, DiskRepo diskRepo, BasketRepo basketRepo) {
        this.cloudFileRepo = cloudFileRepo;
        this.diskRepo = diskRepo;
        this.basketRepo = basketRepo;
    }


    public void createRoot(User user) {
        String path = FILE_PATH + user.getUsername();
        File file = createDir(path);
        createDir(file.getPath()+"\\basket");
        createDir(file.getPath()+"\\disk");
    }

    public File createDir(String path){
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new DirNotCreatedException("Dir not created");
            }
        } else {
            throw new DirAlreadyExistException("Dir already exist");
        }
        return file;
    }


    // найти файл на диске
    public String getFullUserDiskPath(CloudFile cloudFile, User user) {
        String path = "\\" + cloudFile.getName();
        while (cloudFile.getParent() != null) {
            cloudFile = cloudFileRepo.findById(cloudFile.getParent().getId()).get();
            path = "\\" + cloudFile.getName() + path;
        }
        return FILE_PATH + user.getUsername() + "\\disk" + path;
    }

    public CloudFile getCloudFileById(Long id) {
        return cloudFileRepo.findById(id).get();
    }


    // Добавить директорию
    public CloudFile saveDir(CloudFileDto cloudFileDto, User user) {
        CloudFile cloudFile = CloudFileDto.toCloudFile(cloudFileDto);

        if(cloudFileDto.getParentId() != null) {
            CloudFile parent = cloudFileRepo.findById(cloudFileDto.getParentId()).get();
            cloudFile.setParent(parent);
        }
        String path = getFullUserDiskPath(cloudFile, user);
        createDir(path);
        cloudFile.setDisk(user.getDisk());
        cloudFile.setBasket(user.getBasket());

        return cloudFileRepo.save(cloudFile);
    }

    public CloudFile saveFile(MultipartFile file, String filename, User user, Long parentId) throws IOException {
        CloudFile cloudFile = new CloudFile();
        if (cloudFileRepo.findByParentIdAndNameAndDiskId(parentId, filename, user.getDisk().getId()) != null) {
            throw new FileAlreadyExistsException("file already exist");
        }
        if (parentId != null) {
            cloudFile.setParent(cloudFileRepo.findById(parentId).get());
        }
        cloudFile.setSize(file.getSize());
        cloudFile.setName(filename);
        cloudFile.setType(filename.split("[.]")[1]);
        cloudFile.setDisk(user.getDisk());

        String path = getFullUserDiskPath(cloudFile, user);

        System.out.println(filename);
        System.out.println(path);


        File convertFile = new File(path);
        if(!convertFile.exists()) {
            convertFile.createNewFile();
        }
        FileOutputStream fout = new FileOutputStream(convertFile);
        fout.write(file.getBytes());
        fout.close();

        return cloudFileRepo.save(cloudFile);
    }

    public List<CloudFile> getByParentIdAndDiskId(Long parentId, Long diskId) {
        return cloudFileRepo.findCloudFilesByParentIdAndDiskId(parentId, diskId);
    }

    public CloudFile getById(Long parentId) {
        return cloudFileRepo.findById(parentId).get();
    }

    public Resource downloadFile(CloudFile cloudFile, User user) throws MalformedURLException {
        Path path = Paths.get(getFullUserDiskPath(cloudFile, user));
        Resource resource = new UrlResource(path.toUri());
        return resource;
    }

    public void deleteFile(CloudFile cloudFile, User user) throws IOException {
        String path = getFullUserDiskPath(cloudFile, user);
        File file = new File(path);
        if (cloudFile.getType().equals("dir")) {
            FileUtils.deleteDirectory(file);
        } else {
            file.delete();
        }
        cloudFileRepo.deleteById(cloudFile.getId());
    }
}
