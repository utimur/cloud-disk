package com.example.clouddisk.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "basket")
@Data
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User basketUser;

    @OneToMany(mappedBy = "basket")
    private List<CloudFile> cloudFiles;
}
