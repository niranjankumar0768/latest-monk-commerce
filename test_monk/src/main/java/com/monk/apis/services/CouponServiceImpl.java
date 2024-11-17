package com.monk.apis.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.apis.dto.CouponRequestDto;
import com.monk.apis.dto.CouponResponseDto;
import com.monk.apis.enitiy.Cart;
import com.monk.apis.enitiy.CartItem;
import com.monk.apis.enitiy.Coupon;
import com.monk.apis.exception.CustomException;
import com.monk.apis.mapper.CouponMapper;
import com.monk.apis.repo.CartRepository;
import com.monk.apis.repo.CouponRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private CouponMapper couponMapper;

	@Autowired
	private CartRepository cartRepository;

	@Override
	@Transactional(readOnly = true)
	public List<CouponResponseDto> getAllCoupons() {
		log.info("getAllCoupons()");
		return couponRepository.findAll().stream().map(couponMapper::mapToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public CouponResponseDto getCouponById(String couponId) {
		log.info("getCouponById({})", couponId);
		Optional<Coupon> coupon = couponRepository.findByUuid(couponId);

		if (coupon.isPresent()) {
			return couponMapper.mapToDto(coupon.get());
		} else {
			throw new CustomException("Coupon not found by id ");
		}
	}

	@Override
	@Transactional
	public void createCoupon(CouponRequestDto couponRequestDto) {
		log.info("createCoupon({})", couponRequestDto);
		Coupon coupon = couponMapper.mapToEntity(couponRequestDto);
		couponRepository.save(coupon);
	}

	@Override
	@Transactional
	public void updateCoupon(String couponId, CouponRequestDto couponRequestDto) {
		log.info("updateCoupon({}, {})", couponId, couponRequestDto);
		Optional<Coupon> coupon = couponRepository.findByUuid(couponId);

		if (coupon.isPresent()) {
			Coupon c = coupon.get();
			c = couponMapper.updateToEntity(couponRequestDto, c);
		} else {
			throw new CustomException("Coupone not found by id ");
		}
	}

	@Override
	@Transactional
	public void deleteCoupon(String couponId) {
		log.info("deleteCoupon({})", couponId);

		Optional<Coupon> coupon = couponRepository.findByUuid(couponId);

		if (coupon.isPresent()) {
			couponRepository.delete(coupon.get());
		} else {
			throw new CustomException("Coupone not found by id ");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CouponResponseDto> applicableCoupon(Cart cart) {
		log.info("applicableCoupon()");

		return couponRepository.findAll().stream().filter(c -> Boolean.TRUE.equals(c.isActive()))
				.filter(coupon -> isCouponApplicable(cart, coupon)).map(coupon -> calculateDiscount(cart, coupon))
				.collect(Collectors.toList());
	}

	private boolean isCouponApplicable(Cart cart, Coupon coupon) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode details = mapper.readTree(coupon.getDetails());

			switch (coupon.getType()) {
			case CART_WISE:
				double totalAmount = cart.calculateTotalPrice();
				return totalAmount > details.get("threshold").asDouble();
			case PRODUCT_WISE:
				long productId = details.get("product_id").asLong();
				return cart.getItems().stream().anyMatch(item -> item.getProductId().equals(productId));
			case BxGy:
				return checkBxGyCondition(cart, details);
			default:
				return false;
			}
		} catch (JsonProcessingException e) {
			throw new CustomException("Invalid coupon details format", e);
		}
	}

	private boolean checkBxGyCondition(Cart cart, JsonNode details) {
		Map<Long, Integer> productQuantities = cart.getItems().stream()
				.collect(Collectors.toMap(CartItem::getProductId, CartItem::getQuantity));

		for (JsonNode buyProduct : details.get("buy_products")) {
			long productId = buyProduct.get("product_id").asLong();
			int requiredQuantity = buyProduct.get("quantity").asInt();
			if (productQuantities.getOrDefault(productId, 0) < requiredQuantity) {
				return false;
			}
		}
		return true;
	}

	@Override
	@Transactional
	public Cart applyCoupon(String couponId, Cart cart) {
		log.info("applyCoupon({})", couponId);

		Coupon coupon = couponRepository.findByUuid(couponId)
				.orElseThrow(() -> new CustomException("Coupon not found for ID: " + couponId));

		if (!isCouponApplicable(cart, coupon)) {
			throw new CustomException("Coupon not applicable to this cart");
		}

		applyDiscountToCart(cart, coupon);

		cart.setTotalDiscount(cart.getItems().stream().mapToDouble(CartItem::getTotalDiscount).sum());
		cart.setTotalPrice(cart.calculateTotalPrice() - cart.getTotalDiscount());

		return cartRepository.save(cart);
	}

	private void applyDiscountToCart(Cart cart, Coupon coupon) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode details = mapper.readTree(coupon.getDetails());

			switch (coupon.getType()) {
			case CART_WISE:
				double discount = cart.calculateTotalPrice() * details.get("discount").asDouble() / 100;
				cart.setTotalDiscount(discount);
				break;
			case PRODUCT_WISE:
				long productId = details.get("product_id").asLong();
				double productDiscount = details.get("discount").asDouble();
				cart.getItems().stream().filter(item -> item.getProductId().equals(productId)).forEach(
						item -> item.setTotalDiscount(item.getPrice() * item.getQuantity() * productDiscount / 100));
				break;
			case BxGy:
				applyBxGyDiscount(cart, details);
				break;
			}
		} catch (JsonProcessingException e) {
			throw new CustomException("Invalid coupon details format", e);
		}
	}

	private void applyBxGyDiscount(Cart cart, JsonNode details) {
		Map<Long, Integer> productQuantities = cart.getItems().stream()
				.collect(Collectors.toMap(CartItem::getProductId, CartItem::getQuantity));

		int repetitions = StreamSupport.stream(details.get("buy_products").spliterator(), false)
				.mapToInt(buyProduct -> productQuantities.getOrDefault(buyProduct.get("product_id").asLong(), 0)
						/ buyProduct.get("quantity").asInt())
				.min().orElse(0);

		StreamSupport.stream(details.get("get_products").spliterator(), false).forEach(getProduct -> {
			long productId = getProduct.get("product_id").asLong();
			int freeQuantity = repetitions * getProduct.get("quantity").asInt();
			double price = cart.getItemPrice(productId);
			cart.addItem(new CartItem(null, cart, productId, freeQuantity, price, 0));
		});
	}

	private CouponResponseDto calculateDiscount(Cart cart, Coupon coupon) {
		double discount = 0;

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode details = mapper.readTree(coupon.getDetails());

			switch (coupon.getType()) {
			case CART_WISE:
				double totalAmount = cart.calculateTotalPrice();
				discount = totalAmount * details.get("discount").asDouble() / 100;
				break;
			case PRODUCT_WISE:
				long productId = details.get("product_id").asLong();
				discount = cart.getItems().stream().filter(item -> item.getProductId().equals(productId))
						.mapToDouble(
								item -> item.getPrice() * item.getQuantity() * details.get("discount").asDouble() / 100)
						.sum();
				break;
			case BxGy:
				discount = calculateBxGyDiscount(cart, details);
				break;
			}

			return couponMapper.mapToDto(coupon);
		} catch (JsonProcessingException e) {
			throw new CustomException("Invalid coupon details format", e);
		}
	}

	private double calculateBxGyDiscount(Cart cart, JsonNode details) {
		Map<Long, Integer> productQuantities = cart.getItems().stream()
				.collect(Collectors.toMap(CartItem::getProductId, CartItem::getQuantity));

		int repetitions = StreamSupport.stream(details.get("buy_products").spliterator(), false)
				.mapToInt(buyProduct -> productQuantities.getOrDefault(buyProduct.get("product_id").asLong(), 0)
						/ buyProduct.get("quantity").asInt())
				.min().orElse(0);

		return StreamSupport
				.stream(details.get("get_products").spliterator(), false).mapToDouble(getProduct -> repetitions
						* getProduct.get("quantity").asInt() * cart.getItemPrice(getProduct.get("product_id").asLong()))
				.sum();
	}

}
