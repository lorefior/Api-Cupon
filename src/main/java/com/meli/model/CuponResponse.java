package com.meli.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CuponResponse {
	
	@JsonProperty(value = "item_ids")
	private List<String> itemsIdList;
	private Float total;
	
	public List<String> getItemsIdList() {
		return itemsIdList;
	}
	public void setItemsIdList(List<String> itemsIdList) {
		this.itemsIdList = itemsIdList;
	}
	public Float getTotal() {
		return total;
	}
	public void setTotal(Float total) {
		this.total = total;
	}
	
}
