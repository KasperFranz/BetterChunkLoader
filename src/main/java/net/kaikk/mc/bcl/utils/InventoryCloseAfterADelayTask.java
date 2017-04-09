package net.kaikk.mc.bcl.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;

/**
 * Created by KasperFranz on 19-02-2017.
 */
public class InventoryCloseAfterADelayTask implements Consumer<Task> {

    private Player player;

    public InventoryCloseAfterADelayTask(Player player) {
        this.player = player;
    }

    @Override
    public void accept(Task task) {
        player.closeInventory(Cause.of(NamedCause.simulated(player)));
    }
}