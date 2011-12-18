package examples.jerry;

import jodd.io.FileUtil;
import jodd.io.NetUtil;
import jodd.lagarto.dom.jerry.Jerry;
import jodd.util.SystemUtil;

import java.io.File;
import java.io.IOException;

public class ChangeGooglePage {

	public static void main(String[] args) throws IOException {
		File file = new File(SystemUtil.getTempDir(), "google.html");
		NetUtil.downloadFile("http://google.com", file);

		Jerry doc = Jerry.jerry(FileUtil.readString(file));
		
		doc.$("div#mngb").detach();
		doc.$("div#lga").html("<b>Google</b>");

		String newHtml = doc.html();
		FileUtil.writeString(new File(SystemUtil.getTempDir(), "google2.html"), newHtml);
	}
}
