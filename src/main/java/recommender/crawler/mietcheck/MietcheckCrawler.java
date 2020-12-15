package recommender.crawler.mietcheck;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import recommender.crawler.WebCrawler;
import recommender.crawler.mietcheck.dto.Plz;
import recommender.crawler.mietcheck.dto.PlzList;

public class MietcheckCrawler implements WebCrawler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MietcheckCrawler.class);

	@Override
	public void crawl(URL url, File file) throws IOException {
		if (url.getHost().equals("www.miet-check.de")) {
			final Document doc = Jsoup.connect(url.toString()).get();
			final Element table = doc.getElementsByClass("table").get(0);
			final Elements rows = table.select("tr");
			final PlzList dto = new PlzList();
			final List<Plz> plzList = new ArrayList<>();

			for (int i = 1; i < rows.size(); i++) {
				final Element row = rows.get(i);
				final Elements cols = row.select("td");

				if (cols != null) {
					plzList.add(createPlzDto(cols));
				}
			}

			if (!plzList.isEmpty()) {
				final ObjectMapper mapper = new ObjectMapper();

				dto.plzList = plzList;
				mapper.writeValue(file, dto);
				LOGGER.info("Succesfully saved json " + file.toString());
			} else {
				LOGGER.warn("No estates could be crawled for " + file.toString());
			}
		}
	}

	private Plz createPlzDto(Elements cols) {
		final Plz result = new Plz();
		final int plz = Integer.valueOf(cols.get(1).text());
		final double price = Double.valueOf(cols.get(2).text().split("Euro")[0]);

		result.plz = plz;
		result.price = price;

		return result;
	}

}
