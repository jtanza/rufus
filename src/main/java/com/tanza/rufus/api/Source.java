package com.tanza.rufus.api;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by jtanza.
 */
public class Source {
    private URL url;
    private boolean frontpage;
    private List<String> tags;

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

