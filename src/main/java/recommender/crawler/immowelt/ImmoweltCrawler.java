package recommender.crawler.immowelt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import recommender.crawler.WebCrawler;
import recommender.crawler.immowelt.dto.Estate;
import recommender.crawler.immowelt.dto.EstatesList;

public class ImmoweltCrawler implements WebCrawler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImmoweltCrawler.class);

	@Override
	public void crawl(URL url, File file) throws IOException {
		if (url.getHost().equals("www.immowelt.de")) {
			final Set<String> estateIDs = getEstateIDs(url);
			final List<Estate> estateDtoList = crawlEstatesByID(estateIDs);
			final EstatesList result = new EstatesList();

			if (estateDtoList != null) {
				final ObjectMapper mapper = new ObjectMapper();

				result.estates = estateDtoList;
				mapper.writeValue(file, result);
				LOGGER.info("Succesfully saved json " + file.toString());
			} else {
				LOGGER.warn("No estates could be crawled for " + file.toString());
			}
		}
	}

	private List<Estate> crawlEstatesByID(Set<String> estateIDs) {
		final List<Estate> result = new ArrayList<>();

		estateIDs.forEach(id -> {
			try {
				result.add(createDtoByID(id));
			} catch (final IOException e) {
				LOGGER.error("Couldnt catch id " + id);
				e.printStackTrace();
			}
		});

		return result;
	}

	private Estate createDtoByID(String id) throws IOException {
		final Estate result = new Estate();
		final URL url = new URL("https://www.immowelt.de/expose/" + id);
		final Document doc = Jsoup.connect(url.toString()).get();
		final Elements quickFactElements = doc.getElementsByClass("quickfacts");
		final Elements locationElements = doc.getElementsByClass("location");
		final Elements hardFactElements = doc.getElementsByClass("hardfacts");
		final Element estateElement = doc.getElementById("divImmobilie");

		if (estateElement == null) {
			throw new IOException("no estateElement for id " + id);
		}
		final Elements contentElements = estateElement.getElementsByClass("section_content");
		final Element flatElement = contentElements.get(1);
		final Element priceElement = doc.getElementsByClass("preise").get(0);

		result.id = id;

		final String title = quickFactElements.get(0).child(0).text();
		result.title = title;

		try {
			final String address = locationElements.get(0).child(0).text();
			result.address = address;
		} catch (final Exception e) {
			LOGGER.warn("Could not read address for id " + id + "\n" + e.getMessage());
		}

		try {
			final String priceColdString = hardFactElements.get(0).child(0).text().split("€")[0].replaceAll(" |\\.", "")
					.replaceAll(",", ".");
			final Double priceCold = Double.valueOf(priceColdString);
			result.priceCold = priceCold;
		} catch (final Exception e) {
			LOGGER.warn("Could not read priceCold for id " + id + "\n" + e.getMessage());
		}

		try {
			final String livingSpaceString = hardFactElements.get(0).child(1).text().split(" ")[0].replaceAll(",", ".");
			final Double livingSpace = Double.valueOf(livingSpaceString);
			result.livingSpace = livingSpace;
		} catch (final Exception e) {
			LOGGER.warn("Could not read livingSpace for id " + id + "\n" + e.getMessage());
		}

		try {
			final String numRoomsString = hardFactElements.get(0).child(2).text().split(" ")[0].replaceAll(",", ".");
			final Double numRooms = Double.valueOf(numRoomsString);
			result.numRooms = numRooms;
		} catch (final Exception e) {
			LOGGER.warn("Could not read numRooms for id " + id + "\n" + e.getMessage());
		}

		final String flatDescription = flatElement.toString();
		result.balcony = flatDescription.contains("Balkon");
		result.garden = flatDescription.contains("Garten");

		try {
			final Elements priceRowElememts = priceElement.getElementsByClass("datarow");
			final Set<Element> rowElementsContainingExtraCost = priceRowElememts.stream()
					.filter(row -> !row.getElementsByClass("iw_left").get(0)
							.getElementsMatchingText("\\bNebenkosten\\b").isEmpty())
					.filter(row -> row.getElementsByClass("iw_left").get(0).getElementsMatchingText("\\bKaltmiete\\b")
							.isEmpty())
					.collect(Collectors.toSet());

			if (rowElementsContainingExtraCost.size() >= 1) {
				final Element extraCostElement = rowElementsContainingExtraCost.iterator().next();
				final Double extraCosts = Double
						.valueOf(extraCostElement.child(1).text().split(" ")[0].replaceAll(",", "."));

				result.extraCosts = extraCosts;
			} else {
				throw new Exception("No extraCost element found.");
			}
		} catch (final Exception e) {
			LOGGER.warn("Could not read extraCosts for id " + id + "\n" + e.getMessage());
		}
		return result;

	}

	private Set<String> getEstateIDs(URL url) throws IOException {
		final Set<String> result = new HashSet<String>();
		final Document doc = Jsoup.connect(url.toString()).get();
		final Elements estates = doc.select("[data-estateid]");
		final Element nextPage = doc.getElementById("nlbPlus");

		estates.forEach(e -> result.add(e.attr("data-estateid")));

		if (nextPage != null) {
			final String baseUrl = url.getHost();
			final String nextPageUrl = nextPage.attr("href");
			final URL nextURL = new URL("https://" + baseUrl + nextPageUrl);

			result.addAll(getEstateIDs(nextURL));
		}

		return result;
	}
}
