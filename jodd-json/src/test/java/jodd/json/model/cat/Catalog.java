// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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