package com.travel.billage.domain.item;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.image.ItemImage;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.rental.Rental;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

@Entity
@Table(
        name = "TB_ITEM",
        indexes = {
                @Index(name = "IDX_ITEM_CATEGORY_ITEM_NO", columnList = "category, item_no")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_no")
    private Long itemNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(length = 2000)
    private String description;

    @Column(name = "rental_point", nullable = false)
    private Integer rentalPoint;

    @Column(name = "rental_place_name", nullable = false, length = 100)
    private String rentalPlaceName;

    @Column(name = "rental_place", nullable = false, length = 255)
    private String rentalPlace;

    @Column(name = "rental_place_detail", length = 255)
    private String rentalPlaceDetail;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemStatus itemStatus;

    @OneToMany(mappedBy = "item")
    private List<Rental> rentals = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<ItemImage> images = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Item(Member member, Category category, String itemName, String description, Integer rentalPoint,
                String rentalPlaceName, String rentalPlace, String rentalPlaceDetail, Double latitude,
                Double longitude) {
        this.member = member;
        this.category = category;
        this.itemName = itemName;
        this.description = description;
        this.rentalPoint = rentalPoint;
        this.rentalPlaceName = rentalPlaceName;
        this.rentalPlace = rentalPlace;
        this.rentalPlaceDetail = rentalPlaceDetail;
        this.latitude = latitude;
        this.longitude = longitude;
        this.itemStatus = ItemStatus.AVAILABLE;
    }

    public void updateItem(Category category, String itemName, String description, Integer rentalPoint,
                            String rentalPlaceName, String rentalPlace, String rentalPlaceDetail, Double latitude,
                            Double longitude) {
        this.category = category;
        this.itemName = itemName;
        this.description = description;
        this.rentalPoint = rentalPoint;
        this.rentalPlaceName = rentalPlaceName;
        this.rentalPlace = rentalPlace;
        this.rentalPlaceDetail = rentalPlaceDetail;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void changeStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }
}
