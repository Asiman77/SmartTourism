package com.ironhack.smarttourism.entity;


import com.ironhack.smarttourism.entity.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{

    @Column(nullable = false , length = 100)
    private String fullName;

    @Column(nullable = false , length = 200 , unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName role;

    @OneToOne(mappedBy = "user" , fetch = FetchType.LAZY)
    private Agency agency;

}
