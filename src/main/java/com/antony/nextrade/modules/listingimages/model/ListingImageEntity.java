package com.antony.nextrade.modules.listingimages.model;

import com.antony.nextrade.common.entities.BaseEntity;
import com.antony.nextrade.modules.listing.model.ListingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "listing_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListingImageEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "listing_id")
    private ListingEntity listing;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;
}
