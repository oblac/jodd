// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.model.cat;

public class Price {

	int amount;
	Long audienceSubCategoryId;
	Long seatCategoryId;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Long getAudienceSubCategoryId() {
		return audienceSubCategoryId;
	}

	public void setAudienceSubCategoryId(Long audienceSubCategoryId) {
		this.audienceSubCategoryId = audienceSubCategoryId;
	}

	public Long getSeatCategoryId() {
		return seatCategoryId;
	}

	public void setSeatCategoryId(Long seatCategoryId) {
		this.seatCategoryId = seatCategoryId;
	}
}