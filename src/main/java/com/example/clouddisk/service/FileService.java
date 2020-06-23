package com.example.clouddisk.service;

import com.example.clouddisk.exceptions.file.DirAlreadyExistException;
import com.example.clouddisk.exceptions.file.DirNotCreatedException;
import com.example.clouddisk.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileService {

    @Value("${file.system.path}")
    private String FILE_PATH;


    public void createRoot(User user) {
        String path = FILE_PATH + user.getUsername();
        File file = createDir(path);
        createDir(file.getPath()+"\\basket");
        createDir(file.getPath()+"\\disk");
    }

    public File createDir(String path){
        File file = new File(path);
        if (!file.exists()) {
            if(!file.mkdir()){
                throw new DirNotCreatedException("Dir not created");
            }
        } else {
            throw new DirAlreadyExistException("Dir already exist");
        }
        return file;
    }
}
