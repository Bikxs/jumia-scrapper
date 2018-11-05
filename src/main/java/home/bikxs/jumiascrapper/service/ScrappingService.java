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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ScrappingService {
    private static Logger LOGGER = LoggerFactory.getLogger(ScrappingService.class);
    private final RestTemplate restTemplate = restTemplate();
    private final String BASE_URL = "https://www.jumia.co.ke/";
    private final HttpHeaders headers = createHeadeers();
    private volatile boolean finished = false;
    @Autowired
    private SlackNotificationService slackNotificationService;
    @Value("${slack.notifications.enabled}")
    private Boolean slack_notifications_enabled;

    private ConcurrentMap<String, Item> itemConcurrentMap = new ConcurrentHashMap<>();
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(200);
    @Value("${application.deepsearch}")
    private Boolean deep_search;
    @Value("${application.verbose.logger}")
    private Boolean DETAILED_LOGS;


    public List<SubCategory> scrapeCategories() {
        LOGGER.info("Started scrape");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> respEntity = restTemplate.exchange(BASE_URL, HttpMethod.GET, entity, String.class);

        String queired = respEntity.getBody();
        List<SubCategory> subCategoryList = new ArrayList<>();
        Document doc = Jsoup.parse(queired);
        Elements elemsCategories = doc.getElementsByClass("categories");

        for (Element elemCategory :
                elemsCategories) {
            Element elemCategoryName = elemCategory.getElementsByClass("category").first();
            String categoryName = null;
            if (elemCategoryName != null) {
                categoryName = elemCategoryName.text();
            }
            Elements elemsSubCategories = elemCategory.getElementsByClass("subcategory");
            for (Element catElement :
                    elemsSubCategories) {
                String name = catElement.text();
                String href = catElement.attr("href");
                SubCategory subCategory = new SubCategory(categoryName, name, href);

                subCategoryList.add(subCategory);
                //System.out.println(subCategory.toString());
            }
        }
        LOGGER.info("Found " + subCategoryList.size() + " subcategories");

        return subCategoryList;
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

        InetSocketAddress address = new InetSocketAddress("172.23.12.226", 4145);
        try {
            if (address.getAddress().isReachable(5000)) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
                requestFactory.setProxy(proxy);
            }
        } catch (IOException e) {
            System.out.println(address.getAddress().getHostAddress() + ": " + e.getMessage());
            ;
        }

        return new RestTemplate(requestFactory);
    }

    public void scrapeCategories(SubCategory subCategory) {
        if (DETAILED_LOGS)
            LOGGER.info("Started SubCategory Thread for " + subCategory.toString());
        executor.execute(new SubCategoryScrapperThread(subCategory, headers, itemConcurrentMap, restTemplate, deep_search, DETAILED_LOGS,slackNotificationService,slack_notifications_enabled));
    }

    public void startFileUpdater() {
        try {
            FileUpdater updater = new FileUpdater();
            updater.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void scrapeItems(List<SubCategory> subcategories) {
        LOGGER.info("Started search @" + Calendar.getInstance().getTime());
        for (SubCategory subCategory :
                subcategories) {
            scrapeCategories(subCategory);
        }
    }

    public class FileUpdater extends Thread implements Runnable {
        private PrintWriter pw;
        private PrintWriter pwFound;

        public FileUpdater() throws IOException {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
            String timeStamp = simpleDateFormat.format(Calendar.getInstance().getTime());
            pw = new PrintWriter(new FileWriter(new File("jumia_items_" + timeStamp + ".csv")));
            pwFound = new PrintWriter(new FileWriter(new File("found_treasures_" + timeStamp + ".csv")));

            pw.println("Category,Subcategory,Item,Price,Discount,href");
            pwFound.println("Type,Category,Subcategory,Item,Price,Discount,href");
        }

        @Override
        public void run() {
            int zeroCount = 0;
            while (!finished) {
                if (executor.getActiveCount() == 0 && executor.getCompletedTaskCount() > 0) {
                    if (executor.getCompletedTaskCount() == executor.getTaskCount()) {
                        finished = true;
                        LOGGER.info("Finished search @" + Calendar.getInstance().getTime());
                        System.exit(0);
                        break;

                    }
                }
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {

                }
                List<Item> items = new ArrayList<>(itemConcurrentMap.values());
                itemConcurrentMap.clear();
                for (Item item :
                        items) {
                    if (item == null) continue;
                    pw.println(item.toCSVString());
                    if (item.isTreasure()) {
                        System.out.println(AnsiColors.green("TREASURE!!! " + item.getPrice()+ " " + item.getSubCategory().getName() + " " + item.getDescription()));
                        pwFound.println("TREASURE,"  + item.toCSVString());
                    } else if (item.isBargain()) {
                        pwFound.println("BARGAIN," + item.toCSVString());
                        //System.out.println(AnsiColors.yellow("BARGAIN!!! ") + item.toString());
                    }
                }

				pw.flush();
				pwFound.flush();
                LOGGER.info("Tasks Active=" + executor.getActiveCount() + " Completed=" + executor.getCompletedTaskCount() + " Total=" + executor.getTaskCount() + "");
                if(items.isEmpty()){
                    if(zeroCount>5){
                        break;
                    }
                    zeroCount++;
                }
            }
			pw.flush();
			pwFound.flush();
            pw.close();
            pwFound.close();

        }
    }
}
