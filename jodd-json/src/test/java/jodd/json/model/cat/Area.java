// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.model.cat;

public class Area {

	Long areaId;
	Long[] blockIds;

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public Long[] getBlockIds() {
		return blockIds;
	}

	public void setBlockIds(Long[] blockIds) {
		this.blockIds = blockIds;
	}
}