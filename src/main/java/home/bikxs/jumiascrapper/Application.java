package home.bikxs.jumiascrapper;

import home.bikxs.jumiascrapper.data.Item;
import home.bikxs.jumiascrapper.data.SubCategory;
import home.bikxs.jumiascrapper.service.ScrappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {
	private static Logger LOGGER = LoggerFactory.getLogger(Application.class);
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
		Collections.shuffle(subcategories);
		for (SubCategory subCategory :
				subcategories) {
			//System.out.println(subCategory.toString());
			scrappingService.scrapeCategories(subCategory);

		}

	}
}
