package com.ironhack.smarttourism.entity;

import com.ironhack.smarttourism.entity.enums.TourStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tour_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourPackage extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(length = 255)
    private String meetingPoint;

    @Column(columnDefinition = "TEXT")
    private String includedServices;

    @Column(columnDefinition = "TEXT")
    private String excludedServices;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status = TourStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;
}
