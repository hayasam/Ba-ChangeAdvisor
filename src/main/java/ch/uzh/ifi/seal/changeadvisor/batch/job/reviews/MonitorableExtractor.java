package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import config.ConfigurationManager;
import crawler.GoogleReviewsCrawler;
import extractors.Extractor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MonitorableExtractor extends Extractor {

    private static final Logger logger = Logger.getLogger(MonitorableExtractor.class);

    private ConfigurationManager configurationManager;

    private Map<String, GoogleReviewsCrawler> crawlers = new HashMap<>();

    private List<Future<?>> crawlersRunning;

    public MonitorableExtractor(ArrayList<String> appsToMine, ConfigurationManager configurationManager) {
        super(appsToMine);
        this.configurationManager = configurationManager;
    }

    @Override
    public void extract() {
        ExecutorService executor = Executors.newFixedThreadPool(this.configurationManager.getNumberOfThreadToUse());

        for (String app : appsToMine) {
            GoogleReviewsCrawler crawler = new GoogleReviewsCrawler(app, configurationManager);
            crawlers.put(app, crawler);
        }

        crawlersRunning = crawlers.values().stream().map(executor::submit).collect(Collectors.toList());

        executor.shutdown();
    }

    public Map<String, Integer> getProgress() {
        Map<String, Integer> progress = new ConcurrentHashMap<>(crawlers.size());
        for (Map.Entry<String, GoogleReviewsCrawler> entry : crawlers.entrySet()) {
            progress.put(entry.getKey(), getReviewsCounter(entry.getValue()));
        }
        return progress;
    }

    private Integer getReviewsCounter(GoogleReviewsCrawler crawler) {
        Integer reviewsCounter = 0;
        try {
            reviewsCounter = (Integer) FieldUtils.readDeclaredField(crawler, "reviewsCounter", true);
        } catch (IllegalAccessException e) {
            logger.error(e);
        }
        return reviewsCounter;
    }

    public boolean isDone() {
        return crawlersRunning.stream().allMatch(Future::isDone);
    }
}