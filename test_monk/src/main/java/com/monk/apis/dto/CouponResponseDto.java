package com.monk.apis.dto;

import com.monk.apis.enums.Type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponResponseDto {

	private String couponId;
	private Type type;
	private String details;
	private boolean active;

}
