package com.example.clouddisk.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

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
    private Boolean isActivated = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;


    @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
    private Disk disk;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
    private Basket basket;


}
