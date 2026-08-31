package com.travel.billage.domain.image;

import com.travel.billage.domain.image.dto.ItemImageResponse;
import com.travel.billage.security.MemberDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/items/{itemNo}/images")
@RequiredArgsConstructor
public class ItemImageController {

    private final ItemImageService itemImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemImageResponse> addImage(@PathVariable Long itemNo,
                                                        @AuthenticationPrincipal MemberDetails principal,
                                                        @RequestParam("file") MultipartFile file,
                                                        @RequestParam(defaultValue = "false") boolean mainImage) {
        ItemImage itemImage = itemImageService.addImage(itemNo, principal.getMemberNo(), file, mainImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemImageResponse.from(itemImage));
    }

    @GetMapping
    public List<ItemImageResponse> getImages(@PathVariable Long itemNo) {
        return itemImageService.getImages(itemNo).stream()
                .map(ItemImageResponse::from)
                .toList();
    }

    @PatchMapping("/{imageNo}/main")
    public ResponseEntity<Void> changeMainImage(@PathVariable Long itemNo, @PathVariable Long imageNo,
                                                 @AuthenticationPrincipal MemberDetails principal) {
        itemImageService.changeMainImage(itemNo, imageNo, principal.getMemberNo());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{imageNo}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long itemNo, @PathVariable Long imageNo,
                                             @AuthenticationPrincipal MemberDetails principal) {
        itemImageService.deleteImage(imageNo, principal.getMemberNo());
        return ResponseEntity.noContent().build();
    }
}
