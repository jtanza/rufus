package com.tanza.rufus.core;

import com.tanza.rufus.feed.FeedConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;

public class UserUtils {

    private UserUtils() {throw new AssertionError();}

    /**
     * Simple check against a {@link User}'s creds.
     *
     * @param email
     * @param password
     * @return
     */
    public static boolean valid(String email, String password) {
        return StringUtils.isNotEmpty(email)
            && StringUtils.isNotEmpty(password)
            && EmailValidator.getInstance().isValid(email);
    }

    /**
     * Ensures a {@link User}'s requested starter feeds are valid.
     *
     * @param feeds
     * @return
     */
    public static boolean validStarterFeeds(List<String> feeds) {
        if (CollectionUtils.isEmpty(feeds)) {
            return false;
        }
        for (String feed : feeds) {
            if (!FeedConstants.STARTER_FEEDS.containsKey(feed)) {
                return false;
            }
        }
        return true;
    }
}
