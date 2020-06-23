package com.example.clouddisk.models;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "file")
@Data
public class CloudFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    private String avatar;
    private String access_link;
    private Long size = 0L;

    @OneToOne
    @JoinColumn(name = "access_id", referencedColumnName = "id")
    private Access access;

    @ManyToOne
    @JoinColumn(name = "basket_id")
    Basket basket;

    @ManyToOne
    @JoinColumn(name = "disk_id")
    Disk disk;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CloudFile parent;

    @OneToMany(mappedBy = "parent")
    private List<CloudFile> cloudFiles;

    private boolean is_dir = false;

}
