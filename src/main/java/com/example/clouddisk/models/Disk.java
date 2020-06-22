package com.example.clouddisk.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "disk")
@Data
public class Disk {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "disk")
    private User user;
}
