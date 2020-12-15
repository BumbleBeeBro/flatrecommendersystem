package recommender.crawler;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public interface WebCrawler {

	abstract void crawl(URL url, File file) throws IOException;

}
