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

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User diskUser;

    @OneToMany(mappedBy = "disk")
    List<CloudFile> cloudFiles;
}
