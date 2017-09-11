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

package jodd.json;

import jodd.datetime.JDateTime;
import jodd.io.StreamUtil;
import jodd.json.fixtures.model.cat.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class CatalogTest {

	protected String dataRoot;

	@BeforeEach
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = JsonParserTest.class.getResource("data");
		if (data != null) {
			dataRoot = data.getFile();
		}
	}

	@Test
	public void testParseCatalogAsObject() throws IOException {
		String json = loadJSON("citm_catalog");

		Catalog catalog = new JsonParser().parse(json, Catalog.class);

		assertCatalog(catalog);
	}

	@Test
	public void testParseCatalogAsObjectWithClassname() throws IOException {
		String json = loadJSON("citm_catalog");

		Catalog catalog = new JsonParser().setClassMetadataName("class").parse(json, Catalog.class);

		assertCatalog(catalog);
	}

	@Test
	public void testParseSerializeCatalogNotDeep() throws IOException {
		String json = loadJSON("citm_catalog");

		Catalog catalog = new JsonParser().parse(json, Catalog.class);

		String newJson = new JsonSerializer().deep(false).serialize(catalog);

		Catalog jsonCatalog = new JsonParser().parse(newJson, Catalog.class);

		assertNull(jsonCatalog.getPerformances());
		assertNull(jsonCatalog.getAreaNames());
		assertNull(jsonCatalog.getEvents());
		assertNull(jsonCatalog.getAudienceSubCategoryNames());
		assertNull(jsonCatalog.getSeatCategoryNames());
		assertNull(jsonCatalog.getSubTopicNames());
		assertNull(jsonCatalog.getTopicNames());
		assertNull(jsonCatalog.getTopicSubTopics());
		assertNull(jsonCatalog.getVenueNames());
	}

	@Test
	public void testParseSerializeCatalog() throws IOException {
		String json = loadJSON("citm_catalog");

		Catalog catalog = new JsonParser().parse(json, Catalog.class);

		String newJson = new JsonSerializer().deep(true).serialize(catalog);

		Catalog jsonCatalog = new JsonParser().parse(newJson, Catalog.class);

		assertCatalog(jsonCatalog);
	}

	@Test
	public void testParseCatalogAsMap() throws IOException {
		String json = loadJSON("citm_catalog");

		Map catalog = new JsonParser()
				.map("values.keys", Long.class)
				.map("venueNames.keys", String.class)
				.useAltPaths()
				.parse(json);

		String newJson = new JsonSerializer().deep(true).serialize(catalog);

		Catalog jsonCatalog = new JsonParser().parse(newJson, Catalog.class);

		assertCatalog(jsonCatalog);
	}


	private void assertCatalog(Catalog catalog) {
		assertNotNull(catalog);

		Map<Long, String> map;

		// areaNames

		map = catalog.getAreaNames();
		assertNotNull(map);
		assertEquals(17, map.size());
		assertEquals("1er balcon jardin", map.get(Long.valueOf(205706005)));

		// audienceSubCategoryNames

		map = catalog.getAudienceSubCategoryNames();
		assertEquals(1, map.size());
		assertEquals("Abonné", map.get(Long.valueOf(337100890)));

		// events

		Map<Long, Event> events = catalog.getEvents();
		assertNotNull(events);
		assertEquals(184, events.size());

		Event event = events.get(Long.valueOf(138586341));
		assertNotNull(event);
		assertNull(event.getDescription());
		assertEquals(138586341, event.getId().longValue());
		assertNull(event.getLogo());
		assertEquals("30th Anniversary Tour", event.getName());
		Long[] subTopicIds = event.getSubTopicIds();
		assertEquals(2, subTopicIds.length);
		assertEquals(337184269, subTopicIds[0].longValue());
		assertEquals(337184283, subTopicIds[1].longValue());
		assertNull(event.getSubjectCode());
		assertNull(event.getSubtitle());
		Long[] topicIds = event.getTopicIds();
		assertEquals(2, topicIds.length);
		assertEquals(324846099, topicIds[0].longValue());
		assertEquals(107888604, topicIds[1].longValue());

		// performances

		List<Performance> performances = catalog.getPerformances();
		assertNotNull(performances);
		assertEquals(243, performances.size());

		Performance performance = performances.get(0);
		assertEquals(138586341, performance.getEventId().longValue());
		assertEquals(339887544, performance.getId().longValue());
		assertNull(performance.getLogo());
		assertNull(performance.getName());
		List<Price> prices = performance.getPrices();
		assertEquals(2, prices.size());
		Price price = prices.get(0);
		assertEquals(90250, price.getAmount());
		assertEquals(337100890, price.getAudienceSubCategoryId().longValue());
		assertEquals(338937295, price.getSeatCategoryId().longValue());
		List<SeatCategory> seatCategories = performance.getSeatCategories();
		assertEquals(2, seatCategories.size());
		SeatCategory seatCategory = seatCategories.get(0);
		assertEquals(338937295, seatCategory.getSeatCategoryId().longValue());
		List<Area> areas = seatCategory.getAreas();
		assertEquals(11, areas.size());
		Area area = areas.get(0);
		assertEquals(205705999, area.getAreaId().longValue());
		assertEquals(0, area.getBlockIds().length);
		JDateTime start = performance.getStart();
		assertEquals(1372701600000L, start.getTimeInMillis());
		assertEquals("PLEYEL_PLEYEL", performance.getVenueCode());

		// seatCategoryNames

		map = catalog.getSeatCategoryNames();
		assertEquals(64, map.size());
		assertEquals("catétgorie unique", map.get(Long.valueOf(342752792)));

		// subTopicNames

		map = catalog.getSubTopicNames();
		assertEquals(19, map.size());
		assertEquals("Jazz", map.get(Long.valueOf(337184269)));

		// topicNames

		map = catalog.getTopicNames();
		assertEquals(4, map.size());
		assertEquals("Genre", map.get(Long.valueOf(324846099)));

		// topicSubTopics

		Map<Long, Long[]> map2 = catalog.getTopicSubTopics();
		assertEquals(4, map2.size());
		Long[] longs = map2.get(Long.valueOf(107888604));
		assertEquals(2, longs.length);
		assertEquals(337184283L, longs[0].longValue());

		// venueNames

		Map<String, String> map3 = catalog.getVenueNames();
		assertEquals(1, map3.size());
		assertEquals("Salle Pleyel", map3.get("PLEYEL_PLEYEL"));
	}

	@Test
	public void test20k() throws IOException {
		String json = loadJSON("20k");

		List<Map<String, Object>> array = new JsonParser().parse(json);

		assertEquals(22, array.size());

		for (int i = 0; i < 22; i++) {
			Map<String, Object> map = array.get(i);

			assertEquals(19, map.size());
			assertEquals(i, ((Integer)map.get("id")).intValue());
		}
	}

	private String loadJSON(String name) throws IOException {
		FileInputStream fis = new FileInputStream(new File(dataRoot, name + ".json.gz"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		StreamUtil.copy(new GZIPInputStream(fis), out);

		String json = out.toString("UTF-8");

		fis.close();

		return json;
	}
}