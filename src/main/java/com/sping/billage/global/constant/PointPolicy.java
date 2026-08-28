package com.sping.billage.global.constant;

/**
 * 포인트 정책 상수. 포인트 금액은 반드시 이 클래스를 통해 참조한다.
 */
public final class PointPolicy {

    /** 회원가입 보너스 적립 포인트 */
    public static final long SIGNUP_BONUS = 1000L;

    public static final String DESC_SIGNUP_BONUS = "회원가입 보너스";
    public static final String DESC_RENTAL_USE = "물품 대여";
    public static final String DESC_RENTAL_CANCEL_REFUND = "대여 취소 환불";
    public static final String DESC_RENTAL_SETTLEMENT = "대여 완료 정산";

    private PointPolicy() {
    }
}
