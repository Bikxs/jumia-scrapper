package home.bikxs.jumiascrapper;


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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScrappingService {
    private static Logger LOGGER = LoggerFactory.getLogger(ScrappingService.class);
    private final RestTemplate restTemplate = restTemplate();
    private final String BASE_URL = "https://www.jumia.co.ke/";
    private final HttpHeaders headers = createHeadeers();

    public List<SubCategory> scrapeCategories() {
        LOGGER.info("Started scrape");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> respEntity = restTemplate.exchange(BASE_URL, HttpMethod.GET, entity, String.class);
        String queired = respEntity.getBody();
        Document doc = Jsoup.parse(queired);
        Elements catElements = doc.getElementsByClass("subcategory");
        List<SubCategory> subCategoryList = new ArrayList<>();
        for (Element catElement :
                catElements) {
            String name = catElement.text();
            String href = catElement.attr("href");
            SubCategory subCategory = new SubCategory(name, href);

            subCategoryList.add(subCategory);
            //System.out.println(subCategory.toString());
        }
        LOGGER.info("Found " + subCategoryList.size() + " subcategories");
        return subCategoryList;
    }

    public List<Item> scrapeCategories(SubCategory subCategory) {
        int i = 0;
        List<Item> items = new ArrayList<>();
        while (true) {
            i++;
            String url = subCategory.getHref() + (i == 1 ? "" : "?page=" + i);
            //LOGGER.info(url);
            //random wait
            try {
                Thread.sleep(1500L + ((long) Math.random() * 5000));
            } catch (InterruptedException e) {
                System.out.println("Interrupted " + e.getMessage());
            }
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> respEntity=null;
            try{
                respEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            }catch (Exception ex){
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
                //System.out.println(item.toString());
                items.add(item);
            }

        }
        //LOGGER.info("Found " + items.size() + " items for " + subCategory.getName());
        return items;
    }

    private Double parse(Element element) {
        if (element == null) return null;
        String text = element.text().replace("%", "").replace("KSh", "").replace(",", "").trim();
        Double result = null;
        try{
            result = Double.parseDouble(text);
        }
        catch (Exception ex)
        {
            LOGGER.error(text + ": " +  ex.getMessage());
        }
        return result;
    }

    private static HttpHeaders createHeadeers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "www.jumia.co.ke");
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.2; rv:20.0) Gecko/20121202 Firefox/20.0");
        headers.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.add("Accept-Language", "en-US,en;q=0.5");
        headers.add("Referer", "https://www.jumia.co.ke");
        headers.add("Connection", "keep-alive");
        return headers;
    }

    private RestTemplate restTemplate() {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("172.23.12.226", 4145));
        requestFactory.setProxy(proxy);

        return new RestTemplate(requestFactory);
    }
}
