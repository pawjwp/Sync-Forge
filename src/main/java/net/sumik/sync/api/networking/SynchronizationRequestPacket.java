package net.sumik.sync.api.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.sumik.sync.Sync;
import net.sumik.sync.api.shell.ServerShell;
import net.sumik.sync.api.shell.ShellState;
import net.sumik.sync.common.utils.BlockPosUtil;
import net.sumik.sync.common.utils.WorldUtil;

import java.util.UUID;

public class SynchronizationRequestPacket implements ServerPlayerPacket {
    private UUID shellUuid;

    public SynchronizationRequestPacket() {
    }

    public SynchronizationRequestPacket(ShellState shell) {
        this.shellUuid = shell == null ? null : shell.getUuid();
    }

    public SynchronizationRequestPacket(UUID shellUuid) {
        this.shellUuid = shellUuid;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(Sync.MOD_ID, "packet.shell.synchronization.request");
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        if (this.shellUuid == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            buffer.writeUUID(this.shellUuid);
        }
    }

    @Override
    public void read(FriendlyByteBuf buffer) {
        this.shellUuid = buffer.readBoolean() ? buffer.readUUID() : null;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayer player, NetworkEvent.Context ctx) {
        ServerShell shell = (ServerShell)player;
        ShellState state = shell.getShellStateByUuid(this.shellUuid);
        BlockPos currentPos = player.blockPosition();
        Level currentWorld = player.level();
        ResourceLocation currentWorldId = WorldUtil.getId(currentWorld);
        Direction currentFacing = BlockPosUtil.getHorizontalFacing(currentPos, currentWorld).orElse(player.getDirection().getOpposite());

        if (state == null) {
            player.sendSystemMessage(Component.literal("Shell not found"));
            new SynchronizationResponsePacket(currentWorldId, currentPos, currentFacing, currentWorldId, currentPos, currentFacing, null).send(player);
        } else {
            ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, state.getWorld());
            ServerLevel targetWorld = server.getLevel(worldKey);
            if (targetWorld != null) {
                ChunkPos chunkPos = new ChunkPos(state.getPos());
                targetWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, player.getId());
                targetWorld.getChunk(chunkPos.x, chunkPos.z);
            }

            shell.sync(state).ifLeft(storedState -> {
                ResourceLocation targetWorldId = state.getWorld();
                BlockPos targetPos = state.getPos();
                Direction targetFacing = player.getDirection().getOpposite();
                new SynchronizationResponsePacket(currentWorldId, currentPos, currentFacing, targetWorldId, targetPos, targetFacing, storedState).send(player);
            }).ifRight(failureReason -> {
                player.sendSystemMessage(failureReason.toText());
                new SynchronizationResponsePacket(currentWorldId, currentPos, currentFacing, currentWorldId, currentPos, currentFacing, null).send(player);
            });
        }
    }}
