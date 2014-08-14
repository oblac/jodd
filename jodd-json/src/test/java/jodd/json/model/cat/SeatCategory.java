// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.model.cat;

import java.util.List;

public class SeatCategory {

	private List<Area> areas;
	private Long seatCategoryId;

	public List<Area> getAreas() {
		return areas;
	}

	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}

	public Long getSeatCategoryId() {
		return seatCategoryId;
	}

	public void setSeatCategoryId(Long seatCategoryId) {
		this.seatCategoryId = seatCategoryId;
	}
}