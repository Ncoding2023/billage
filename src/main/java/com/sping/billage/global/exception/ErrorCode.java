package com.sping.billage.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // 인증/인가
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 회원
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    // 물품
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "물품을 찾을 수 없습니다."),
    ITEM_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "대여할 수 없는 상태의 물품입니다."),
    ITEM_DELETE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "대여가 진행 중인 물품은 삭제할 수 없습니다."),
    NOT_ITEM_OWNER(HttpStatus.FORBIDDEN, "물품 소유자만 수행할 수 있습니다."),

    // 대여
    RENTAL_NOT_FOUND(HttpStatus.NOT_FOUND, "대여 정보를 찾을 수 없습니다."),
    CANNOT_RENT_OWN_ITEM(HttpStatus.BAD_REQUEST, "본인의 물품은 대여할 수 없습니다."),
    INVALID_RENTAL_PERIOD(HttpStatus.BAD_REQUEST, "대여 기간이 올바르지 않습니다."),
    RENTAL_PERIOD_OVERLAPPED(HttpStatus.CONFLICT, "해당 기간에 이미 대여가 존재합니다."),
    INVALID_RENTAL_STATUS(HttpStatus.BAD_REQUEST, "현재 대여 상태에서는 처리할 수 없습니다."),
    NOT_RENTAL_RENTER(HttpStatus.FORBIDDEN, "대여 신청자만 수행할 수 있습니다."),

    // 반납
    RETURN_NOT_FOUND(HttpStatus.NOT_FOUND, "반납 정보를 찾을 수 없습니다."),
    RETURN_ALREADY_REQUESTED(HttpStatus.CONFLICT, "이미 반납 신청이 접수되었습니다."),

    // 포인트
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트 잔액이 부족합니다."),

    // 문의
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "문의를 찾을 수 없습니다."),
    INQUIRY_ALREADY_ANSWERED(HttpStatus.CONFLICT, "이미 답변이 완료된 문의입니다."),

    // 파일
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않는 파일 형식입니다.");

    private final HttpStatus status;
    private final String message;
}
