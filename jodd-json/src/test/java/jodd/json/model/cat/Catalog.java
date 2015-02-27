// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.model.cat;

import java.util.List;
import java.util.Map;

public class Catalog {

	private Map<Long, String> areaNames;
	private Map<Long, String> audienceSubCategoryNames;
	private Map<Long, Event> events;
	private List<Performance> performances;
	private Map<Long, String> seatCategoryNames;
	private Map<Long, String> subTopicNames;
	private Map<Long, String> topicNames;
	private Map<Long, Long[]> topicSubTopics;
	private Map<String, String> venueNames;


	public Map<Long, String> getAreaNames() {
		return areaNames;
	}

	public void setAreaNames(Map<Long, String> areaNames) {
		this.areaNames = areaNames;
	}

	public Map<Long, String> getAudienceSubCategoryNames() {
		return audienceSubCategoryNames;
	}

	public void setAudienceSubCategoryNames(Map<Long, String> audienceSubCategoryNames) {
		this.audienceSubCategoryNames = audienceSubCategoryNames;
	}

	public Map<Long, Event> getEvents() {
		return events;
	}

	public void setEvents(Map<Long, Event> events) {
		this.events = events;
	}

	public List<Performance> getPerformances() {
		return performances;
	}

	public void setPerformances(List<Performance> performances) {
		this.performances = performances;
	}

	public Map<Long, String> getSeatCategoryNames() {
		return seatCategoryNames;
	}

	public void setSeatCategoryNames(Map<Long, String> seatCategoryNames) {
		this.seatCategoryNames = seatCategoryNames;
	}

	public Map<Long, String> getSubTopicNames() {
		return subTopicNames;
	}

	public void setSubTopicNames(Map<Long, String> subTopicNames) {
		this.subTopicNames = subTopicNames;
	}

	public Map<Long, String> getTopicNames() {
		return topicNames;
	}

	public void setTopicNames(Map<Long, String> topicNames) {
		this.topicNames = topicNames;
	}

	public Map<Long, Long[]> getTopicSubTopics() {
		return topicSubTopics;
	}

	public void setTopicSubTopics(Map<Long, Long[]> topicSubTopics) {
		this.topicSubTopics = topicSubTopics;
	}

	public Map<String, String> getVenueNames() {
		return venueNames;
	}

	public void setVenueNames(Map<String, String> venueNames) {
		this.venueNames = venueNames;
	}
}