package com.ironhack.smarttourism.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false , updatable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false , updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = updatedAt;
    }
}
