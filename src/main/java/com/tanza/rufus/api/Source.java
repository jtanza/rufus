package com.tanza.rufus.api;

import com.tanza.rufus.core.User;
import com.tanza.rufus.db.SourceMapper;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

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
}

