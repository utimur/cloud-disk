package com.example.clouddisk.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "usr")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    private String mail;

    private Long freeSpace = new Long(0);

    private Boolean hasAvatar = false;

//
//    // Заменить на OneToOne
//    private Long basket_id;
//    // Заменить на OneToOne
//    private Long disk_id;

}
