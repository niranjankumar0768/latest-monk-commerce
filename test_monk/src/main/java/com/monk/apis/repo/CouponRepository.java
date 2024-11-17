package com.monk.apis.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monk.apis.enitiy.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

	Optional<Coupon> findByUuid(String uuid);

}
