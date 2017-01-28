package net.kaikk.mc.bcl.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

/**
 * Created by Rob5Underscores on 10/12/2016.
 */
public abstract interface Configurable {

    void setup();

    void load();

    void save();

    void populate();

    CommentedConfigurationNode get();
}
