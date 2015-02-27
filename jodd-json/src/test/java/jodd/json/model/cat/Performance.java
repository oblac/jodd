// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.model.cat;

import jodd.datetime.JDateTime;

import java.util.List;

public class Performance {

	private Long eventId;
	private Long id;
	private String logo;
	private String name;
	private List<Price> prices;
	private List<SeatCategory> seatCategories;
	private String seatMapImage;
	private JDateTime start;
	private String venueCode;

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Price> getPrices() {
		return prices;
	}

	public void setPrices(List<Price> prices) {
		this.prices = prices;
	}

	public List<SeatCategory> getSeatCategories() {
		return seatCategories;
	}

	public void setSeatCategories(List<SeatCategory> seatCategories) {
		this.seatCategories = seatCategories;
	}

	public String getSeatMapImage() {
		return seatMapImage;
	}

	public void setSeatMapImage(String seatMapImage) {
		this.seatMapImage = seatMapImage;
	}

	public JDateTime getStart() {
		return start;
	}

	public void setStart(JDateTime start) {
		this.start = start;
	}

	public String getVenueCode() {
		return venueCode;
	}

	public void setVenueCode(String venueCode) {
		this.venueCode = venueCode;
	}
}