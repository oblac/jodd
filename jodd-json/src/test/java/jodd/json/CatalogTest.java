// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.datetime.JDateTime;
import jodd.io.StreamUtil;
import jodd.json.model.cat.Area;
import jodd.json.model.cat.Catalog;
import jodd.json.model.cat.Event;
import jodd.json.model.cat.Performance;
import jodd.json.model.cat.Price;
import jodd.json.model.cat.SeatCategory;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CatalogTest {

	protected String dataRoot;

	@Before
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
		String json = loadJSON();

		Catalog catalog = new JsonParser().parse(json, Catalog.class);

		assertCatalog(catalog);
	}

	@Test
	public void testParseCatalogAsObjectWithClassname() throws IOException {
		String json = loadJSON();

		Catalog catalog = new JsonParser().setClassMetadataName("class").parse(json, Catalog.class);

		assertCatalog(catalog);
	}

	@Test
	public void testParseSerializeCatalog() throws IOException {
		String json = loadJSON();

		Catalog catalog = new JsonParser().parse(json, Catalog.class);

		String newJson = new JsonSerializer().includeCollections(true).serialize(catalog);

		Catalog jsonCatalog = new JsonParser().parse(newJson, Catalog.class);

		assertCatalog(jsonCatalog);
	}

	@Test
	public void testParseCatalogAsMap() throws IOException {
		String json = loadJSON();

		Map catalog = new JsonParser()
				.map("values.keys", Long.class)
				.map("venueNames.keys", String.class)
				.useAltPaths()
				.parse(json);

		String newJson = new JsonSerializer().includeCollections(true).serialize(catalog);

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


	private String loadJSON() throws IOException {
		FileInputStream fis = new FileInputStream(new File(dataRoot, "citm_catalog.json.gz"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		StreamUtil.copy(new GZIPInputStream(fis), out);

		String json = out.toString("UTF-8");

		fis.close();

		return json;
	}
}