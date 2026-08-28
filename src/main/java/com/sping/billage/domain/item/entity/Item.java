package com.sping.billage.domain.item.entity;

import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.enums.ItemStatus;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.place.entity.Place;
import com.sping.billage.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "ITEM")
@SequenceGenerator(name = "ITEM_SEQ_GEN", sequenceName = "ITEM_SEQ", allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ITEM_SEQ_GEN")
    @Column(name = "ITEM_ID")
    private Long id;

    @Column(name = "NAME", length = 200, nullable = false)
    private String name;

    @Lob
    @Column(name = "DESCRIPTION", columnDefinition = "CLOB")
    private String description;

    @Column(name = "RENTAL_POINT", nullable = false)
    private Long rentalPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private ItemStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORY", length = 20, nullable = false)
    private ItemCategory category;

    @Column(name = "THUMBNAIL_PATH", length = 200)
    private String thumbnailPath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OWNER_ID", nullable = false)
    private Member owner;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Place place;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImage> images = new ArrayList<>();

    @Builder
    private Item(String name, String description, Long rentalPoint, ItemCategory category,
                 String thumbnailPath, Member owner) {
        this.name = name;
        this.description = description;
        this.rentalPoint = rentalPoint;
        this.category = category;
        this.thumbnailPath = thumbnailPath;
        this.owner = owner;
        this.status = ItemStatus.AVAILABLE;
    }

    public void update(String name, String description, Long rentalPoint, ItemCategory category) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (rentalPoint != null) {
            this.rentalPoint = rentalPoint;
        }
        if (category != null) {
            this.category = category;
        }
    }

    public void changeThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public void changeStatus(ItemStatus status) {
        this.status = status;
    }

    public void assignPlace(Place place) {
        this.place = place;
        place.assignItem(this);
    }

    public void addImage(ItemImage image) {
        this.images.add(image);
        image.assignItem(this);
    }

    public void clearImages() {
        this.images.clear();
    }

    public boolean isOwnedBy(Long memberId) {
        return this.owner.getId().equals(memberId);
    }

    public boolean isAvailable() {
        return this.status == ItemStatus.AVAILABLE;
    }
}
