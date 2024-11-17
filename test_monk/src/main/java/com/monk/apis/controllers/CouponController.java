package com.monk.apis.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.apis.dto.CouponRequestDto;
import com.monk.apis.dto.CouponResponseDto;
import com.monk.apis.enitiy.Cart;
import com.monk.apis.enums.Type;
import com.monk.apis.exception.CustomException;
import com.monk.apis.services.CouponService;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

	@Autowired
	private CouponService couponService;

	// 1. Get all coupons
	@GetMapping
	public ResponseEntity<List<CouponResponseDto>> getAllCoupons() {
		List<CouponResponseDto> coupons = couponService.getAllCoupons();
		return ResponseEntity.ok(coupons);
	}

	// 2. Get a specific coupon by ID
	@GetMapping("/{couponId}")
	public ResponseEntity<CouponResponseDto> getCouponById(@PathVariable String couponId) {
		CouponResponseDto coupon = couponService.getCouponById(couponId);
		return ResponseEntity.ok(coupon);
	}

	// 3. Create a new coupon
	@PostMapping
	public ResponseEntity<String> createCoupon(@RequestBody Map<String, Object> payload) {
		ObjectMapper objectMapper = new ObjectMapper();
		CouponRequestDto couponRequestDto = new CouponRequestDto();

		try {
			couponRequestDto.setType(Type.valueOf((String) payload.get("type")));
			couponRequestDto.setActive((Boolean) payload.get("active"));
			couponRequestDto.setDetails(objectMapper.writeValueAsString(payload.get("details")));
		} catch (JsonProcessingException e) {
			throw new CustomException("Invalid details format", e);
		}

		couponService.createCoupon(couponRequestDto);
		return ResponseEntity.ok("Coupon created successfully");
	}

	// 4. Update a specific coupon
	@PutMapping("/{couponId}")
	public ResponseEntity<String> updateCoupon(@PathVariable String couponId,
			@RequestBody CouponRequestDto couponRequestDto) {
		couponService.updateCoupon(couponId, couponRequestDto);
		return ResponseEntity.ok("Coupon updated successfully");
	}

	// 5. Delete a specific coupon
	@DeleteMapping("/{couponId}")
	public ResponseEntity<String> deleteCoupon(@PathVariable String couponId) {
		couponService.deleteCoupon(couponId);
		return ResponseEntity.ok("Coupon deleted successfully");
	}

	// 6. Fetch applicable coupons for a given cart
	@PostMapping("/applicable")
	public ResponseEntity<List<CouponResponseDto>> getApplicableCoupons(@RequestBody Cart cart) {
		List<CouponResponseDto> applicableCoupons = couponService.applicableCoupon(cart);
		return ResponseEntity.ok(applicableCoupons);
	}

	// 7. Apply a specific coupon to the cart
	@PostMapping("/apply/{couponId}")
	public ResponseEntity<Cart> applyCoupon(@PathVariable String couponId, @RequestBody Cart cart) {
		Cart updatedCart = couponService.applyCoupon(couponId, cart);
		return ResponseEntity.ok(updatedCart);
	}
}