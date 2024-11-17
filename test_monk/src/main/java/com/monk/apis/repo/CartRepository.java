package com.monk.apis.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monk.apis.enitiy.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
