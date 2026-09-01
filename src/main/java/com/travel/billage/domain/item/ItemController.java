package com.travel.billage.domain.item;

import com.travel.billage.common.dto.PageResponse;
import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.item.dto.ItemCreateRequest;
import com.travel.billage.domain.item.dto.ItemResponse;
import com.travel.billage.domain.item.dto.ItemUpdateRequest;
import com.travel.billage.security.MemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    private static final int DEFAULT_PAGE_SIZE = 10;

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
    public PageResponse<ItemResponse> getItems(@RequestParam(required = false) Category category,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        int pageSize = size > 0 ? size : DEFAULT_PAGE_SIZE;
        var pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "itemNo"));
        return PageResponse.from(itemService.getItems(category, keyword, pageable).map(ItemResponse::from));
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
