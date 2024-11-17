package com.monk.apis.dto;

import com.monk.apis.enums.Type;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponRequestDto {

	@NotNull(message = "NotNull.couponRequestDto.type")
	private Type type;

	private String details;

	private boolean active;

}
