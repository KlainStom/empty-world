package com.github.klainstom.emptyworld;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.biomes.Biome;

import java.util.Arrays;
import java.util.List;

public class ExtensionMain extends Extension {
    private InstanceContainer instanceContainer;
    @Override
    public void initialize() {
        MinecraftServer.LOGGER.info("$name$ initialize.");

        instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer();
        instanceContainer.setChunkGenerator(new EmptyGenerator());
        instanceContainer.enableAutoChunkLoad(false);
        instanceContainer.loadChunk(0,0);

        instanceContainer.setTimeUpdate(null);
        instanceContainer.setTime(-6000);

        getEventNode().addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(8.5, 1, 8.5));
        });

        getEventNode().addListener(AddEntityToInstanceEvent.class, event -> {
            if (!event.getInstance().getUniqueId().equals(instanceContainer.getUniqueId())) return;
            if (!(event.getEntity() instanceof Player)) return;
            final Player player = (Player) event.getEntity();
            player.setAutoViewable(false);
            player.setGameMode(GameMode.ADVENTURE);
        });

        getEventNode().addListener(EntityDamageEvent.class, event -> {
            if (!event.getEntity().getInstance().getUniqueId().equals(instanceContainer.getUniqueId())) return;
            if (!(event.getEntity() instanceof Player)) return;
            if (event.getDamageType().equals(DamageType.VOID)) {
                event.getEntity().teleport(((Player) event.getEntity()).getRespawnPoint());
                event.setCancelled(true);
            }
        });
    }

    @Override
    public void terminate() {
        MinecraftServer.LOGGER.info("$name$ terminate.");
        instanceContainer.getPlayers().forEach(player -> player.kick(Component.text("This instance was destroyed.")));
        MinecraftServer.getInstanceManager().unregisterInstance(instanceContainer);
    }

    private static class EmptyGenerator implements ChunkGenerator {

        @Override
        public void generateChunkData(ChunkBatch batch, int chunkX, int chunkZ) {
            batch.setBlock(8, 0, 8, Block.STONE);
        }

        @Override
        public void fillBiomes(Biome[] biomes, int chunkX, int chunkZ) {
            Arrays.fill(biomes, Biome.PLAINS);
        }

        @Override
        public List<ChunkPopulator> getPopulators() {
            return null;
        }
    }
}
