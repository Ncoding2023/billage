package com.travel.billage.domain.item;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.item.dto.ItemCreateRequest;
import com.travel.billage.domain.item.dto.ItemResponse;
import com.travel.billage.domain.item.dto.ItemUpdateRequest;
import com.travel.billage.security.MemberDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> registerItem(@AuthenticationPrincipal MemberDetails principal,
                                                       @Valid @RequestBody ItemCreateRequest request) {
        Item item = itemService.registerItem(
                principal.getMemberNo(), request.category(), request.itemName(), request.description(),
                request.rentalPoint(), request.rentalPlaceName(), request.rentalPlace(),
                request.rentalPlaceDetail(), request.latitude(), request.longitude());
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.from(item));
    }

    @GetMapping("/{itemNo}")
    public ItemResponse getItem(@PathVariable Long itemNo) {
        return ItemResponse.from(itemService.getItem(itemNo));
    }

    @GetMapping
    public List<ItemResponse> getItems(@RequestParam(required = false) Category category,
                                        @RequestParam(required = false) String keyword) {
        List<Item> items;
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        if (category != null && hasKeyword) {
            items = itemService.searchItemsByCategory(category, keyword);
        } else if (category != null) {
            items = itemService.getItemsByCategory(category);
        } else if (hasKeyword) {
            items = itemService.searchItems(keyword);
        } else {
            items = itemService.getAllItems();
        }
        return items.stream().map(ItemResponse::from).toList();
    }

    @PatchMapping("/{itemNo}")
    public ItemResponse updateItem(@PathVariable Long itemNo, @AuthenticationPrincipal MemberDetails principal,
                                    @Valid @RequestBody ItemUpdateRequest request) {
        itemService.updateItem(itemNo, principal.getMemberNo(), request.category(), request.itemName(),
                request.description(), request.rentalPoint(), request.rentalPlaceName(), request.rentalPlace(),
                request.rentalPlaceDetail(), request.latitude(), request.longitude());
        return ItemResponse.from(itemService.getItem(itemNo));
    }

    @PatchMapping("/{itemNo}/status")
    public ItemResponse changeStatus(@PathVariable Long itemNo, @AuthenticationPrincipal MemberDetails principal,
                                      @RequestParam ItemStatus status) {
        itemService.changeStatus(itemNo, principal.getMemberNo(), status);
        return ItemResponse.from(itemService.getItem(itemNo));
    }

    @DeleteMapping("/{itemNo}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemNo, @AuthenticationPrincipal MemberDetails principal) {
        itemService.deleteItem(itemNo, principal.getMemberNo());
        return ResponseEntity.noContent().build();
    }
}
