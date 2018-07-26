package home.bikxs.jumiascrapper;

import home.bikxs.jumiascrapper.data.SubCategory;
import home.bikxs.jumiascrapper.service.ScrappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class Application implements CommandLineRunner {
	private static Logger LOGGER = LoggerFactory.getLogger(Application.class);
	@Value("${application.scrap_cycle}")
	private Long scrapDuration;
	@Autowired
	private ScrappingService scrappingService;
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("Application for Scrapping Jumia");
		scrappingService.startFileUpdater();

		List<SubCategory> subcategories = scrappingService.scrapeCategories();

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				LOGGER.info("Scrapping all items every 5 mins task");
				Collections.shuffle(subcategories);
				scrappingService.scrapeItems(subcategories);
			}
		}, 0, scrapDuration);
	}
}
