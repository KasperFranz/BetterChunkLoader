package guru.franz.mc.bcl.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.transformation.ConfigurationTransformation;
import ninja.leaping.configurate.transformation.MoveStrategy;

public class Transformations {

    private static final int CONFIG_LATEST = 1;
    private static final String VERSION_KEY = "config-version";

    public static ConfigurationTransformation versionedTransformation() {
        return ConfigurationTransformation.versionedBuilder()
                .setVersionKey(VERSION_KEY)
                .addVersion(CONFIG_LATEST, firstConfig())
                .build();
    }

    /**
     * @return A {@link ConfigurationTransformation} that standardizes config-version 1 from no config version (-1).
     */
    public static ConfigurationTransformation firstConfig() {
        return getBuilder()
                .addAction(path("item"), (path, value) -> path("items"))
                .addAction(path("items", "type"), (path, value) -> path("items", "selector"))
                .addAction(path("DataStore"), (path, value) -> {
                    if (value instanceof CommentedConfigurationNode) {
                        CommentedConfigurationNode commentedNode = (CommentedConfigurationNode) value;
                        commentedNode.setComment("Available data storage types include: MySQL, H2.");
                    }
                    return null;
                })
                .build();
    }

    private static ConfigurationTransformation.Builder getBuilder() {
        return ConfigurationTransformation.builder()
                .setMoveStrategy(MoveStrategy.OVERWRITE);
    }

    private static Object[] path(Object... path) {
        return path;
    }
}
