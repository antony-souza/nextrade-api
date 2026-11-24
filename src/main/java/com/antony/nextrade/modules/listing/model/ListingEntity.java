package com.antony.nextrade.modules.listing.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.antony.nextrade.common.entities.BaseEntity;
import com.antony.nextrade.modules.categories.model.CategoryEntity;
import com.antony.nextrade.modules.games.model.GameEntity;
import com.antony.nextrade.modules.listingimages.model.ListingImageEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "listings")
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
public class ListingEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bannerUrl;

    @Column(nullable = false)
    private String description;

    @PositiveOrZero
    @Digits(integer = 17, fraction = 2)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @PositiveOrZero
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "game_id")
    private GameEntity game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    private CategoryEntity category;

    @OneToMany(
        mappedBy = "listing",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<ListingImageEntity> listingImages = new ArrayList<>();
}
