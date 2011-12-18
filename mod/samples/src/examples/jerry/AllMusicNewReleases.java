// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.jerry;

import jodd.io.FileUtil;
import jodd.io.NetUtil;
import jodd.lagarto.dom.jerry.Jerry;
import jodd.lagarto.dom.jerry.JerryFunction;
import jodd.util.SystemUtil;

import java.io.File;
import java.io.IOException;

public class AllMusicNewReleases {

	public static void main(String[] args) throws IOException {
		File file = new File(SystemUtil.getTempDir(), "allmusic.html");
		NetUtil.downloadFile("http://allmusic.com", file);

		Jerry doc = Jerry.jerry(FileUtil.readString(file));

		doc.$("div#new_releases div.list_item").each(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {
				System.out.println("-----");
				System.out.println($this.$("div.album_title").text());
				System.out.println($this.$("div.album_artist").text().trim());
				return true;
			}
		});
	}
}
