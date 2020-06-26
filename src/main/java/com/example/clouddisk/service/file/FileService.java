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
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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

//    public CloudFile saveFile(CloudFileDto cloudFileDto, User user) throws IOException {
//        FileWriter fw = new FileWriter( "sample1.txt" );
//        fw.close();
//
//        FileReader fr = new FileReader( "sample2.txt" );
//        fr.close();
//    }

}
