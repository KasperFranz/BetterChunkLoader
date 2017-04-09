package net.kaikk.mc.bcl.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Rob5Underscores on 10/12/2016.
 */
public class Utilities {

    public static UUID getUUIDFromName(String name) {
        Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(name);
        if (onlinePlayer.isPresent()) {
            return onlinePlayer.get().getUniqueId();
        } else {
            Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
            Optional<User> optUser = userStorage.get().get(name);
            if (optUser.isPresent()) {
                User user = optUser.get();
                return user.getUniqueId();
            }
        }
        return null;
    }

    public static Player getPlayerFromName(String name) {
        Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(name);
        if (onlinePlayer.isPresent()) {
            return onlinePlayer.get();
        } else {
            Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
            Optional<User> optUser = userStorage.get().get(name);
            if (optUser.isPresent()) {
                User user = optUser.get();
                if (user.getPlayer().isPresent()) {
                    return user.getPlayer().get();
                }
            }
        }
        return null;
    }
}
