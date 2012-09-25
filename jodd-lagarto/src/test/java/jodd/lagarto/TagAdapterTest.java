// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.io.FileUtil;
import jodd.lagarto.adapter.StripHtmlTagAdapter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class TagAdapterTest {

	protected String testAdapterRoot;

	@Before
	public void setUp() throws Exception {
		if (testAdapterRoot != null) {
			return;
		}

		URL data = LagartoParserTest.class.getResource("adaptert");
		testAdapterRoot = data.getFile();
	}

	@Test
	public void testCleanHtml() throws IOException {
		File ff = new File(testAdapterRoot, "clean.html");

		LagartoParser lagartoParser = new LagartoParser(FileUtil.readString(ff));

		StringBuilder out = new StringBuilder();
		TagWriter tagWriter = new TagWriter(out);
		StripHtmlTagAdapter stripHtmlTagAdapter = new StripHtmlTagAdapter(tagWriter);
		lagartoParser.parse(stripHtmlTagAdapter);

		ff = new File(testAdapterRoot, "clean-out.html");

		assertEquals(FileUtil.readString(ff), out.toString());
	}

	@Test
	public void testTwoAdapters() throws IOException {
		File ff = new File(testAdapterRoot, "two.html");

		LagartoParser lagartoParser = new LagartoParser(FileUtil.readString(ff));
		StringBuilder out = new StringBuilder();
		TagWriter tagWriter = new TagWriter(out);

		TagAdapter tagAdapter1 = new TagAdapter(tagWriter) {
			@Override
			public void tag(Tag tag) {
				if (tag.getType().isStartingTag()) {
					String tagname = tag.getName();
					if (tagname.equals("title")) {
						String id = tag.getAttributeValue("id", false);
						tag.setAttribute("id", false, String.valueOf(Integer.parseInt(id) + 1));
					}
				}
				super.tag(tag);
			}
		};


		TagAdapter tagAdapter2 = new TagAdapter(tagAdapter1) {
			@Override
			public void tag(Tag tag) {
				if (tag.getType().isStartingTag()) {
					String tagname = tag.getName();
					if (tagname.equals("title")) {
						tag.addAttribute("id", "172");
					}
				}
				super.tag(tag);
			}
		};


		lagartoParser.parse(tagAdapter2);
		ff = new File(testAdapterRoot, "two-out.html");
		assertEquals(FileUtil.readString(ff), out.toString());
	}

}
