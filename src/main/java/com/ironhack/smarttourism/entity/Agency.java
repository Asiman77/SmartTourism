package com.ironhack.smarttourism.entity;


import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "agencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Agency extends BaseEntity {

    @Column(nullable = false , unique = true , length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(unique = true , nullable = false , length = 200)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgencyStatus status = AgencyStatus.PENDING;

    @OneToOne
    @JoinColumn(name = "user_id" , nullable = false , unique = true)
    private User user;
}
