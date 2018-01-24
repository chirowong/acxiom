package com.acxiom.interview.request;

import com.acxiom.interview.enums.PageOrderType;

import java.io.Serializable;

public class ApiRequestOrder implements Serializable {

	private static final long serialVersionUID = -7240429269951352682L;

	private String field;
	private PageOrderType orderType;

	public ApiRequestOrder() {
	}

	public ApiRequestOrder(String field, PageOrderType orderType) {
		this.field = field;
		this.orderType = orderType;
	}
	
	public String getField() {
		return field;
	}

	public PageOrderType getOrderType() {
		return orderType;
	}
}
