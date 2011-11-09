// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.io.FileUtil;
import jodd.lagarto.adapter.StripHtmlTagAdapter;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TagAdapterTest extends TestCase {

	protected String testAdapterRoot;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (testAdapterRoot!= null) {
			return;
		}

		URL data = LagartoParserTest.class.getResource("adaptert");
		testAdapterRoot = data.getFile();
	}

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

	public void testTwoAdapters() throws IOException {
		File ff = new File(testAdapterRoot, "two.html");

		LagartoParser lagartoParser = new LagartoParser(FileUtil.readString(ff));
		StringBuilder out = new StringBuilder();
		TagWriter tagWriter = new TagWriter(out);

		TagAdapter tagAdapter1 = new TagAdapter(tagWriter) {
			@Override
			public void tag(Tag tag) {
				if (tag.getType().isOpeningTag()) {
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
				if (tag.getType().isOpeningTag()) {
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
