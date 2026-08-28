package com.sping.billage.domain.place.entity;

import com.sping.billage.domain.item.entity.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "PLACE")
@SequenceGenerator(name = "PLACE_SEQ_GEN", sequenceName = "PLACE_SEQ", allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLACE_SEQ_GEN")
    @Column(name = "PLACE_ID")
    private Long id;

    @Column(name = "ADDRESS", length = 100)
    private String address;

    @Column(name = "DETAIL_ADDRESS", length = 100)
    private String detailAddress;

    @Column(name = "LATITUDE", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 10, scale = 7)
    private BigDecimal longitude;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ITEM_ID", nullable = false, unique = true)
    private Item item;

    @Builder
    private Place(String address, String detailAddress, BigDecimal latitude, BigDecimal longitude, Item item) {
        this.address = address;
        this.detailAddress = detailAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.item = item;
    }

    public void update(String address, String detailAddress, BigDecimal latitude, BigDecimal longitude) {
        if (address != null) {
            this.address = address;
        }
        if (detailAddress != null) {
            this.detailAddress = detailAddress;
        }
        if (latitude != null) {
            this.latitude = latitude;
        }
        if (longitude != null) {
            this.longitude = longitude;
        }
    }

    public void assignItem(Item item) {
        this.item = item;
    }
}
