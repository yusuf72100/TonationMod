package net.sultan.tonation.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class SharedItemManager {
    private static final Map<UUID, List<SharedItem>> sharedItems = new HashMap<>();
    private static int tickCounter = 0;

    public static void addSharedItem(EntityPlayer player, ItemStack item) {
        UUID playerId = player.getUniqueID();
        List<SharedItem> items = sharedItems.computeIfAbsent(playerId, k -> new ArrayList<>());
        items.add(new SharedItem(item.copy(), System.currentTimeMillis()));

        // Limiter à 10 items maximum
        if (items.size() > 10) {
            items.remove(0);
        }
    }

    public static List<SharedItem> getSharedItems(UUID playerId) {
        return sharedItems.getOrDefault(playerId, new ArrayList<>());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
            // Nettoyer les items expirés toutes les 5 secondes (100 ticks)
            if (tickCounter % 100 == 0) {
                clearExpiredItems();
            }
        }
    }

    private static void clearExpiredItems() {
        long currentTime = System.currentTimeMillis();
        long expireTime = 10 * 60 * 1000; // 10 minutes

        sharedItems.values().forEach(items ->
                items.removeIf(item -> currentTime - item.timestamp > expireTime)
        );
    }

    public static class SharedItem {
        public final ItemStack item;
        public final long timestamp;

        public SharedItem(ItemStack item, long timestamp) {
            this.item = item;
            this.timestamp = timestamp;
        }
    }
}
