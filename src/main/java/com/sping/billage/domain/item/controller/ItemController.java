package com.sping.billage.domain.item.controller;

import com.sping.billage.domain.item.dto.ItemCreateRequest;
import com.sping.billage.domain.item.dto.ItemDetailResponse;
import com.sping.billage.domain.item.dto.ItemListResponse;
import com.sping.billage.domain.item.dto.ItemUpdateRequest;
import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.enums.ItemStatus;
import com.sping.billage.domain.item.service.ItemService;
import com.sping.billage.global.common.ApiResponse;
import com.sping.billage.global.common.PageResponse;
import com.sping.billage.security.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Item", description = "물품 등록 / 조회 / 수정 / 삭제")
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "물품 등록",
            description = "로그인한 사용자만 등록할 수 있다. 대표 이미지 1장(thumbnail)과 상세 이미지 N장(images)을 함께 전송한다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ItemDetailResponse>> createItem(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @ModelAttribute ItemCreateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        ItemDetailResponse response =
                itemService.createItem(principal.getMemberId(), request, thumbnail, images);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "물품이 등록되었습니다."));
    }

    @Operation(summary = "물품 목록 조회", description = "카테고리 / 상태 / 키워드로 검색하며 페이징된다. 로그인 없이 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ItemListResponse>>> getItems(
            @Parameter(description = "카테고리") @RequestParam(required = false) ItemCategory category,
            @Parameter(description = "물품 상태") @RequestParam(required = false) ItemStatus status,
            @Parameter(description = "물품명 검색 키워드") @RequestParam(required = false) String keyword,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.success(
                itemService.getItems(category, status, keyword, pageable)));
    }

    @Operation(summary = "물품 상세 조회", description = "물품 정보와 이미지 목록, 보관 장소(위경도), 소유자 닉네임을 반환한다.")
    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemDetailResponse>> getItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success(itemService.getItem(itemId)));
    }

    @Operation(summary = "물품 수정",
            description = "소유자만 수정할 수 있다. 전달한 항목만 변경되며, 이미지를 전송하면 기존 이미지를 대체한다.")
    @PatchMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ItemDetailResponse>> updateItem(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long itemId,
            @Valid @ModelAttribute ItemUpdateRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        ItemDetailResponse response =
                itemService.updateItem(principal.getMemberId(), itemId, request, thumbnail, images);

        return ResponseEntity.ok(ApiResponse.success(response, "물품이 수정되었습니다."));
    }

    @Operation(summary = "물품 삭제", description = "소유자만 삭제할 수 있고, 대여가 진행 중이면 삭제할 수 없다.")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long itemId) {

        itemService.deleteItem(principal.getMemberId(), itemId);
        return ResponseEntity.ok(ApiResponse.<Void>success(null, "물품이 삭제되었습니다."));
    }
}
