package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Iterator;

public class ReviewReader implements ItemReader<Review> {

    private final ReviewRepository repository;

    private final String appName;

    private static final int PAGE_SIZE = 50;

    private Page<Review> currentPage;

    private Iterator<Review> contentIterator;

    public ReviewReader(ReviewRepository repository, String app) {
        this.repository = repository;
        this.appName = app;
    }

    @Override
    public Review read() throws Exception {
        return readNext();
    }

    private Review readNext() {
        if (isNotYetInitialized()) {
            readFirstPage();
        }

        if (hasReadAllItemsOrIsEmpty()) {
            contentIterator = null;
            return null;
        }

        if (hasContentToRead()) {
            readCurrentPage();
        } else if (isDoneWithCurrentPage()) {
            readNextPage();
        }

        return contentIterator.next();
    }

    private boolean isNotYetInitialized() {
        return currentPage == null;
    }

    private void readFirstPage() {
        currentPage = repository.findByAppName(appName, new PageRequest(0, PAGE_SIZE));
    }

    private boolean hasReadAllItemsOrIsEmpty() {
        return (contentIterator == null && !currentPage.hasNext()) || (contentIterator != null && !contentIterator.hasNext() && !currentPage.hasNext());
    }

    private boolean hasContentToRead() {
        return contentIterator == null && currentPage.hasContent();
    }

    private void readCurrentPage() {
        contentIterator = currentPage.getContent().iterator();
    }

    private boolean isDoneWithCurrentPage() {
        return !contentIterator.hasNext() && currentPage.hasNext();
    }

    private void readNextPage() {
        currentPage = repository.findByAppName(appName, currentPage.nextPageable());
        contentIterator = currentPage.getContent().iterator();
    }
}
