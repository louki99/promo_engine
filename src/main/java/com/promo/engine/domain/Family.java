package com.promo.engine.domain;

import com.promo.engine.enums.FamilyType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "families")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyType familyType;
    
    private String description;
    
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FamilyMember> members;
} 