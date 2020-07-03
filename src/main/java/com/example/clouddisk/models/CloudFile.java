package com.example.clouddisk.models;

import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
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

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @OneToOne
    @JoinColumn(name = "access_id", referencedColumnName = "id")
    private Access access;

    @ManyToOne
    @JoinColumn(name = "basket_id")
    private Basket basket;

    @ManyToOne
    @JoinColumn(name = "disk_id")
    private Disk disk;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private CloudFile parent;

    @OneToMany(cascade = CascadeType.ALL ,mappedBy = "parent")
    private List<CloudFile> cloudFiles;


    @Override
    public String toString() {
        return "CloudFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
