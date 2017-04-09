package net.kaikk.mc.bcl.config;

import net.kaikk.mc.bcl.BetterChunkLoader;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

/**
 * Created by Rob5Underscores on 10/12/2016.
 */
public class Config implements Configurable {

    private static Config config = new Config();
    private Path configFile = Paths.get(BetterChunkLoader.instance().getConfigDir() + "/config.conf", new String[0]);
    private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
    private CommentedConfigurationNode configNode;

    public static Config getConfig() {
        return config;
    }

    public void setup() {
        if (!Files.exists(this.configFile, new LinkOption[0])) {
            try {
                Files.createFile(this.configFile, new FileAttribute[0]);
                load();
                populate();
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            load();
        }
    }

    public void load() {
        try {
            this.configNode = ((CommentedConfigurationNode) this.configLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.configLoader.save(this.configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populate() {
        get().getNode("ServerName").setValue("aServer").setComment("Unique name of server");
        get().getNode("MaxHoursOffline").setValue(72).setComment("Time in hours before player's chunkloaders become inactive.");
        get().getNode("DataStore").setValue("MySQL").setComment("Currently the only DataStore...");
        get().getNode("DefaultChunksAmount", "World").setValue(0);
        get().getNode("DefaultChunksAmount", "Personal").setValue(0);
        get().getNode("MaxChunksAmount", "World").setValue(250);
        get().getNode("MaxChunksAmount", "Personal").setValue(250);

        get().getNode("MySQL", "Hostname").setValue("host");
        get().getNode("MySQL", "Username").setValue("user");
        get().getNode("MySQL", "Password").setValue("pass");
        get().getNode("MySQL", "Database").setValue("db");
    }

    public CommentedConfigurationNode get() {
        return this.configNode;
    }
}
