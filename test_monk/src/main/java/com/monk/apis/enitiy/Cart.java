package com.monk.apis.enitiy;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items;

	private double totalPrice;
	private double totalDiscount;

	public double calculateTotalPrice() {
		return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
	}

	public void addItem(CartItem item) {
		this.items.add(item);
		item.setCart(this);
	}

	public double getItemPrice(long productId) {
		return this.items.stream().filter(item -> item.getProductId() == productId).findFirst().map(CartItem::getPrice)
				.orElse(0.0);
	}

}