package com.tanza.rufus.api;

import com.sun.syndication.feed.synd.SyndFeed;
import com.tanza.rufus.core.User;
import com.tanza.rufus.db.SourceMapper;
import com.tanza.rufus.feed.FeedUtils;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * A {@link Source} is the internal representation of a web feed (RSS)
 * that a {@link User} has subscribed to. Persisted to and parsed from
 * the db via {@link SourceMapper}.
 *
 * e.g. http://rss.nytimes.com/services/xml/rss/nyt/Technology.xml
 *
 * Created by jtanza.
 */
public class Source implements Serializable {
    private static final long serialVersionUID = 1L;

    private URL url;
    private boolean frontpage;
    private List<String> tags;

    public Source() {}

    public Source(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public boolean isFrontpage() {
        return frontpage;
    }

    public void setFrontpage(boolean frontpage) {
        this.frontpage = frontpage;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * POJO used to represent a {@link Source} for display on the client.
     */
    public static class ClientSource implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String sourceName;
        private final String url;
        private final boolean frontpage;
        private final String guid;

        private ClientSource(String sourceName, String url, boolean frontpage) {
            this.sourceName = sourceName;
            this.url = url;
            this.frontpage = frontpage;
            this.guid = UUID.randomUUID().toString();
        }

        public static ClientSource ofExisting(Source source) {
            SyndFeed syndFeed = RufusFeed.generate(source).getFeed();
            return new ClientSource(FeedUtils.clean(syndFeed.getTitle()), source.getUrl().toExternalForm(), source.isFrontpage());
        }

        public String getSourceName() {
            return sourceName;
        }

        public String getUrl() {
            return url;
        }

        public boolean isFrontpage() {
            return frontpage;
        }

        public String getGuid() {
            return guid;
        }
    }
}

