// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.model.cat;

public class Event {

	private String description;
	private Long id;
	private String logo;
	private String name;
	private Long[] subTopicIds;
	private Integer subjectCode;
	private String subtitle;
	private Long[] topicIds;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Long[] getSubTopicIds() {
		return subTopicIds;
	}

	public void setSubTopicIds(Long[] subTopicIds) {
		this.subTopicIds = subTopicIds;
	}

	public Integer getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(Integer subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Long[] getTopicIds() {
		return topicIds;
	}

	public void setTopicIds(Long[] topicIds) {
		this.topicIds = topicIds;
	}
}