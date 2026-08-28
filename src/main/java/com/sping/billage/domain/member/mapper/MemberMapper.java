package com.sping.billage.domain.member.mapper;

import com.sping.billage.domain.member.dto.MemberResponse;
import com.sping.billage.domain.member.dto.SignupRequest;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MemberMapper {

    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "nickname", source = "request.nickname")
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "role", source = "role")
    Member toEntity(SignupRequest request, String encodedPassword, MemberRole role);

    @Mapping(target = "id", source = "member.id")
    @Mapping(target = "email", source = "member.email")
    @Mapping(target = "nickname", source = "member.nickname")
    @Mapping(target = "role", source = "member.role")
    @Mapping(target = "createdAt", source = "member.createdAt")
    @Mapping(target = "pointBalance", source = "pointBalance")
    MemberResponse toResponse(Member member, long pointBalance);
}
