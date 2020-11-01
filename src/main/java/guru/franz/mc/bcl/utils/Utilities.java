package guru.franz.mc.bcl.utils;

import guru.franz.mc.bcl.exception.UserNotFound;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Optional;
import java.util.UUID;

public class Utilities {

    public static User getUserFromUUID(UUID uuid) throws UserNotFound {
        return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(uuid).orElseThrow(UserNotFound::new);
    }

    public static int getOptionOrDefault(User player, String key, int defaultValue){
        Optional<String> option = player.getOption(key);
        return option.map(Integer::parseInt).orElse(defaultValue);

    }
}
