package com.meli.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CuponRequest implements Serializable {
	
	private static final long serialVersionUID = 6409805112118995735L;
	
	@JsonProperty(value = "item_ids")
	private List<String> itemIdList;
	private Float amount;
	
	public List<String> getItemIdList() {
		return itemIdList;
	}
	public void setItemIdList(List<String> itemIdList) {
		this.itemIdList = itemIdList;
	}
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}
}