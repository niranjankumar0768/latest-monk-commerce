package com.monk.apis.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.monk.apis.dto.CouponRequestDto;
import com.monk.apis.dto.CouponResponseDto;
import com.monk.apis.enitiy.Coupon;

@Mapper(componentModel = "spring")
public interface CouponMapper {

	@Mapping(target = "couponId", source = "coupon.uuid")
	CouponResponseDto mapToDto(Coupon coupon);

	Coupon mapToEntity(CouponRequestDto couponRequestDto);

	Coupon updateToEntity(CouponRequestDto couponRequestDto, @MappingTarget Coupon coupon);

}
