package com.example.clouddisk.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "disk")
@Data
public class Disk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "disk")
    private User user;

    @OneToMany(mappedBy = "disk")
    List<CloudFile> cloudFiles;
}
