package com.tanza.rufus.feed;

import com.tanza.rufus.api.Article;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;

import org.junit.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;

import static org.junit.Assert.*;

/**
 * @author jtanza
 */
public class FeedProcessorImplTest {
    private static final String NYTIMES = "http://rss.nytimes.com/services/xml/rss/nyt/World.xml";

    @Test
    public void testPublicArticles() {
        ArticleDao articleDao = Mockito.mock(ArticleDao.class);
        Mockito.when(articleDao.getPublicSources()).thenReturn(FeedUtils.getPublicSources());
        FeedProcessor processor = FeedProcessorImpl.newInstance(articleDao);
        List<Article> articles = processor.buildArticleCollection();
        assertTrue(!articles.isEmpty());
    }

    @Test
    public void testNoSubscriptions() {
        ArticleDao articleDao = Mockito.mock(ArticleDao.class);
        FeedProcessor processor = FeedProcessorImpl.newInstance(articleDao);
        List<Article> articles = processor.buildArticleCollection(Mockito.mock(User.class));
        assertEquals(Collections.EMPTY_LIST, articles);
    }

    @Test
    public void testUserSubscriptions() throws MalformedURLException {
        ArticleDao articleDao = Mockito.mock(ArticleDao.class);
        Mockito.when(articleDao.hasSubscriptions(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(articleDao.getSources(ArgumentMatchers.anyLong())).thenReturn(Collections.singletonList(new Source(new URL(NYTIMES))));
        FeedProcessor processor = FeedProcessorImpl.newInstance(articleDao);
        List<Article> articles = processor.buildArticleCollection(Mockito.mock(User.class));

        assertTrue(!articles.isEmpty());
        assertThat(
            "Every articles has a description",
            articles,
            everyItem(
                hasProperty("description", not(blankOrNullString()))
            )
        );
    }

    @Test
    public void testFrontPageArticles() throws MalformedURLException {
        ArticleDao articleDao = Mockito.mock(ArticleDao.class);
        Mockito.when(articleDao.hasSubscriptions(ArgumentMatchers.anyLong())).thenReturn(true);
        Source nyTimes = new Source(new URL(NYTIMES));
        nyTimes.setFrontpage(true);
        Mockito.when(articleDao.getSources(ArgumentMatchers.anyLong())).thenReturn(Collections.singletonList(nyTimes));
        FeedProcessor processor = FeedProcessorImpl.newInstance(articleDao);
        List<Article> articles = processor.buildFrontpageCollection(Mockito.mock(User.class));

        Map<String, List<Article>> articleMap = articles.stream().collect(Collectors.groupingBy(Article::getChannelUrl));
        articleMap.entrySet().stream().forEach(e -> assertEquals(FeedConstants.DEFAULT_DOCS_PER_FEED, e.getValue().size()));
    }
}