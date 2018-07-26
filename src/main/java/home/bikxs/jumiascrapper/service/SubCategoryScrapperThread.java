package home.bikxs.jumiascrapper.service;

import home.bikxs.jumiascrapper.AnsiColors;
import home.bikxs.jumiascrapper.data.Item;
import home.bikxs.jumiascrapper.data.SubCategory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class SubCategoryScrapperThread implements Runnable {
    private static Logger LOGGER = LoggerFactory.getLogger(SubCategoryScrapperThread.class);
    private SubCategory subCategory;
    private HttpHeaders headers;
    private RestTemplate restTemplate;
    private ConcurrentMap<String, Item> itemConcurrentMap;
    private Boolean deep_search;
    private Boolean DETAILED_LOGS;

    @Override
    public void run() {
        List<Item> items = scrapeCategories();
        //System.out.println("Scrapped " + items.size() + " items for " + subCategory.getName());
        for (Item item :
                items) {
            itemConcurrentMap.put(item.getHref(), item);
        }
    }

    public SubCategoryScrapperThread(SubCategory subCategory, HttpHeaders headers, ConcurrentMap<String, Item> itemConcurrentMap, RestTemplate restTemplate, Boolean deep_search, Boolean DETAILED_LOGS) {
        this.subCategory = subCategory;
        this.headers = headers;
        this.itemConcurrentMap = itemConcurrentMap;
        this.restTemplate = restTemplate;
        this.deep_search = deep_search;
        this.DETAILED_LOGS = DETAILED_LOGS;
    }

    public List<Item> scrapeCategories() {
        int i = 0;
        List<Item> items = new ArrayList<>();
        while (true) {
            i++;
            String url = subCategory.getHref() + (i == 1 ? "" : "?page=" + i);
            if (DETAILED_LOGS)
                LOGGER.info(Thread.currentThread().getName() + " hit:" + url);
            //random wait
            try {
                //Thread.sleep(1500L + ((long) Math.random() * 5000));
                Thread.sleep(0L + ((long) Math.random() * 1000));
            } catch (InterruptedException e) {
                //System.out.println("Interrupted " + e.getMessage());
            }
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> respEntity = null;
            try {

                respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            } catch (Exception ex) {
                if (DETAILED_LOGS)
                    LOGGER.error(Thread.currentThread().getName() + ex.getMessage());
                break;
            }

            String queired = respEntity.getBody();
            Document doc = Jsoup.parse(queired);
            Elements itmElements = doc.getElementsByClass("sku -gallery");

            if (itmElements.isEmpty())
                break;

            for (Element itmElement :
                    itmElements) {
                //System.out.println(itmElement.text());
                String title = itmElement.getElementsByClass("title").first().text();
                String href = itmElement.getElementsByClass("link").attr("href");
                Double price = parse(itmElement.getElementsByClass("price ").first());
                Double discount = parse(itmElement.getElementsByClass("sale-flag-percent").first());
                Item item = new Item(subCategory, title, href, price, discount);
                if (DETAILED_LOGS)
                    LOGGER.info(Thread.currentThread().getName() + " Scrapped " + (item.toString()));
                items.add(item);
                if (DETAILED_LOGS)
                    if (item.isTreasure()) {
                        System.out.println(AnsiColors.green("TREASURE!!! ") + item.toString());
                    } else if (item.isBargain()) {
                        System.out.println(AnsiColors.yellow("BARGAIN!!! ") + item.toString());
                    }
            }
            if (!deep_search) break;
        }
        if (DETAILED_LOGS)
            LOGGER.info(Thread.currentThread().

                    getName() + " Found " + items.size() + " items for " + subCategory.getName());
        return items;
    }

    private Double parse(Element element) {
        if (element == null) return null;
        String text = element.text().replace("%", "").replace("KSh", "").replace(",", "").trim();
        Double result = null;
        try {
            result = Double.parseDouble(text);
        } catch (Exception ex) {
            if (DETAILED_LOGS)
                LOGGER.error(Thread.currentThread().getName() + ex.getMessage());
        }
        return result;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }
}
