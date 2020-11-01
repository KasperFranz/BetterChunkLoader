package guru.franz.mc.bcl.inventory;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;

public class InventoryCloseAfterADelayTask implements Consumer<Task> {

    private final Player player;

    public InventoryCloseAfterADelayTask(Player player) {
        this.player = player;
    }

    @Override
    public void accept(Task task) {
        player.closeInventory();
    }
}