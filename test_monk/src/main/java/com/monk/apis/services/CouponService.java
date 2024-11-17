package com.monk.apis.services;

import java.util.List;

import com.monk.apis.dto.CouponRequestDto;
import com.monk.apis.dto.CouponResponseDto;
import com.monk.apis.enitiy.Cart;

public interface CouponService {

	List<CouponResponseDto> getAllCoupons();

	CouponResponseDto getCouponById(String couponId);

	void createCoupon(CouponRequestDto couponRequestDto);

	void updateCoupon(String couponId, CouponRequestDto couponRequestDto);

	void deleteCoupon(String couponId);

	List<CouponResponseDto> applicableCoupon(Cart cart);

	Cart applyCoupon(String couponId, Cart cart);

}
