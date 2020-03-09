package com.prime.studio.apps.gpsrouteguide.maps.directions.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yalantis
 */
public class Friend {
    private int avatar;
    private String nickname;
    private String link;
    private int background;
    private List<String> interests = new ArrayList<>();

    public Friend(int avatar, String nickname, String link, int background, String... interest) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.background = background;
        this.link = link;
        interests.addAll(Arrays.asList(interest));
    }

    public int getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }
    public String getLink() {
        return link;
    }

    public int getBackground() {
        return background;
    }

    public List<String> getInterests() {
        return interests;
    }
}
