package com.travel.billage.domain.item;

import com.travel.billage.domain.item.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ItemAdminController {

    private final ItemService itemService;

    @PatchMapping("/{itemNo}/status")
    public ItemResponse changeStatus(@PathVariable Long itemNo, @RequestParam ItemStatus status) {
        itemService.adminChangeStatus(itemNo, status);
        return ItemResponse.from(itemService.getItem(itemNo));
    }

    @DeleteMapping("/{itemNo}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemNo) {
        itemService.adminDeleteItem(itemNo);
        return ResponseEntity.noContent().build();
    }
}
