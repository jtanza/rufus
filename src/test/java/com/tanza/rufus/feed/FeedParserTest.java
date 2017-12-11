package com.tanza.rufus.feed;

import com.google.common.collect.Lists;
import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;
import com.tanza.rufus.feed.FeedParser.FeedResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jtanza
 */
public class FeedParserTest {

    private FeedParser feedParser;

    @Before
    public void init() throws MalformedURLException {
        ArticleDao articleDao = Mockito.mock(ArticleDao.class);
        FeedProcessor feedProcessor = Mockito.mock(FeedProcessor.class);
        Mockito.when(articleDao.getSources(ArgumentMatchers.anyLong())).thenReturn(existingSources());
        Mockito.doNothing().when(articleDao).addSource(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        Mockito.doNothing().when(feedProcessor).invalidateCache(ArgumentMatchers.anyLong());

        feedParser = new FeedParser(articleDao, feedProcessor);
    }

    @Test
    public void testValidFeeds() {
        User u = new User();
        u.setId(100L);

        String validFeed = "http://rss.nytimes.com/services/xml/rss/nyt/US.xml";

        List<FeedResponse> parse = feedParser.parse(u, Collections.singletonList(validFeed));
        assertEquals(1, parse.size());
        assertTrue(parse.get(0).isValid());
        assertNull(parse.get(0).getError());
    }

    @Test
    public void testInvalidFeeds() {
        User u = new User();
        u.setId(101L);

        String invalidFeed = "rss.nytimes.com/notvalid";

        List<FeedResponse> parse = feedParser.parse(u, Collections.singletonList(invalidFeed));
        assertEquals(1, parse.size());
        assertFalse(parse.get(0).isValid());
        assertNotNull(parse.get(0).getError());
    }

    @Test
    public void testMessage() {
        User u = new User();
        u.setId(101L);

        List<FeedResponse> parse = feedParser.parse(
            u,
            Lists.newArrayList(
                "http://rss.nytimes.com/services/xml/rss/nyt/US.xml",
                "rss.rufus.com/notvalid", "rss.invalid.com"
            )
        );
        String message = FeedResponse.formatMessage(parse);
        assertNotNull(message);
        System.out.print(message);


    }

    private static List<Source> existingSources() throws MalformedURLException {
        Source s = new Source(new URL("http://rufus.news/feed"));
        s.setFrontpage(true);
        s.setTags(Collections.emptyList());
        return Collections.singletonList(s);
    }
}
