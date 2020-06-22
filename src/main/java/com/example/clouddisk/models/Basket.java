package com.example.clouddisk.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "basket")
@Data
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(mappedBy = "basket")
    private User user;
}
