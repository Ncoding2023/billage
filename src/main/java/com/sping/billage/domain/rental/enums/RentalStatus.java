package com.sping.billage.domain.rental.enums;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum RentalStatus {
    REQUESTED,
    APPROVED,
    IN_PROGRESS,
    RETURN_REQUESTED,
    COMPLETED,
    CANCELLED;

    private static final Set<RentalStatus> ACTIVE =
            Collections.unmodifiableSet(EnumSet.of(REQUESTED, APPROVED, IN_PROGRESS, RETURN_REQUESTED));

    private static final Set<RentalStatus> OCCUPYING =
            Collections.unmodifiableSet(EnumSet.of(APPROVED, IN_PROGRESS, RETURN_REQUESTED));

    /** 아직 종료되지 않은 대여 상태 */
    public static Set<RentalStatus> activeStatuses() {
        return ACTIVE;
    }

    /** 물품 기간을 실제로 점유하는 상태 (기간 겹침 검증 대상) */
    public static Set<RentalStatus> occupyingStatuses() {
        return OCCUPYING;
    }

    public boolean isActive() {
        return ACTIVE.contains(this);
    }
}
