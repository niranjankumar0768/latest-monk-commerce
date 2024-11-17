package com.monk.apis.mapper;

import com.monk.apis.dto.CouponRequestDto;
import com.monk.apis.dto.CouponResponseDto;
import com.monk.apis.enitiy.Coupon;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-17T15:46:48+0530",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.37.0.v20240215-1558, environment: Java 17.0.10 (Eclipse Adoptium)"
)
@Component
public class CouponMapperImpl implements CouponMapper {

    @Override
    public CouponResponseDto mapToDto(Coupon coupon) {
        if ( coupon == null ) {
            return null;
        }

        CouponResponseDto couponResponseDto = new CouponResponseDto();

        couponResponseDto.setCouponId( coupon.getUuid() );
        couponResponseDto.setActive( coupon.isActive() );
        couponResponseDto.setDetails( coupon.getDetails() );
        couponResponseDto.setType( coupon.getType() );

        return couponResponseDto;
    }

    @Override
    public Coupon mapToEntity(CouponRequestDto couponRequestDto) {
        if ( couponRequestDto == null ) {
            return null;
        }

        Coupon coupon = new Coupon();

        coupon.setActive( couponRequestDto.isActive() );
        coupon.setDetails( couponRequestDto.getDetails() );
        coupon.setType( couponRequestDto.getType() );

        return coupon;
    }

    @Override
    public Coupon updateToEntity(CouponRequestDto couponRequestDto, Coupon coupon) {
        if ( couponRequestDto == null ) {
            return null;
        }

        coupon.setActive( couponRequestDto.isActive() );
        coupon.setDetails( couponRequestDto.getDetails() );
        coupon.setType( couponRequestDto.getType() );

        return coupon;
    }
}
