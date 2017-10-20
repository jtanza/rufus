package com.tanza.rufus.feed;

import com.tanza.rufus.api.Source;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.ArticleDao;
import com.tanza.rufus.feed.FeedParser.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

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
        Mockito.doNothing().when(articleDao).addFeed(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());
        Mockito.doNothing().when(feedProcessor).invalidateCache(ArgumentMatchers.anyLong());

        feedParser = new FeedParser(articleDao, feedProcessor);
    }

    @Test
    public void testValidFeeds() {
        User u = new User();
        u.setId(100L);

        String validFeed = "http://rss.nytimes.com/services/xml/rss/nyt/US.xml";

        List<Response> parse = feedParser.parse(u, Collections.singletonList(validFeed));
        Assert.assertEquals(1, parse.size());
        Assert.assertTrue(parse.get(0).isValid());
        Assert.assertNull(parse.get(0).getError());
    }

    @Test
    public void testInvalidFeeds() {
        User u = new User();
        u.setId(101L);

        String invalidFeed = "rss.nytimes.com/notvalid";

        List<Response> parse = feedParser.parse(u, Collections.singletonList(invalidFeed));
        Assert.assertEquals(1, parse.size());
        Assert.assertFalse(parse.get(0).isValid());
        Assert.assertNotNull(parse.get(0).getError());
    }

    private static List<Source> existingSources() throws MalformedURLException {
        Source s = new Source(new URL("http://rufus.news/feed"));
        s.setFrontpage(true);
        s.setTags(Collections.emptyList());
        return Collections.singletonList(s);
    }
}
