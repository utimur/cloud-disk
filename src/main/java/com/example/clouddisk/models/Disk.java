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


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "disk")
    List<CloudFile> cloudFiles;
}
