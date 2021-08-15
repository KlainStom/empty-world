package com.github.klainstom.emptyworld;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.*;
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

        getEventNode().addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(8.5, 61, 8.5));
            player.setAutoViewable(false);
        });
    }

    @Override
    public void terminate() {
        MinecraftServer.LOGGER.info("$name$ terminate.");
        MinecraftServer.getInstanceManager().unregisterInstance(instanceContainer);
    }

    private static class EmptyGenerator implements ChunkGenerator {

        @Override
        public void generateChunkData(ChunkBatch batch, int chunkX, int chunkZ) {
            batch.setBlock(8, 60, 8, Block.STONE);
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
