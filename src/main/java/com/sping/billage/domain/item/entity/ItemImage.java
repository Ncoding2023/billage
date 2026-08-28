package com.sping.billage.domain.item.entity;

import com.sping.billage.global.common.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "ITEM_IMAGE")
@SequenceGenerator(name = "ITEM_IMAGE_SEQ_GEN", sequenceName = "ITEM_IMAGE_SEQ", allocationSize = 1)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImage extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ITEM_IMAGE_SEQ_GEN")
    @Column(name = "ITEM_IMAGE_ID")
    private Long id;

    @Column(name = "ORIGINAL_FILE_NAME", length = 255)
    private String originalFileName;

    @Column(name = "STORED_FILE_NAME", length = 255)
    private String storedFileName;

    @Column(name = "IMAGE_PATH", length = 255)
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;

    @Builder
    private ItemImage(String originalFileName, String storedFileName, String imagePath, Item item) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.imagePath = imagePath;
        this.item = item;
    }

    void assignItem(Item item) {
        this.item = item;
    }
}
