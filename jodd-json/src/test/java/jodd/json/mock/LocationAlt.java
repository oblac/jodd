// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

import jodd.json.JSON2;

@JSON2(strict = true)
public class LocationAlt {

	@JSON2(name="lng")
	private int longitude;
	private int latitude;

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	@JSON2(name="lat")
	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

}