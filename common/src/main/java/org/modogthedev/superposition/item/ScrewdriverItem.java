package org.modogthedev.superposition.item;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.block.AntennaBlock;
import org.modogthedev.superposition.blockentity.PanelBlockEntity;
import org.modogthedev.superposition.blockentity.SignalActorBlockEntity;
import org.modogthedev.superposition.core.SuperpositionBlocks;
import org.modogthedev.superposition.networking.packet.PlayerAttackUseC2SPacket;

public class ScrewdriverItem extends Item {
    public ScrewdriverItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return super.canAttackBlock(state, level, pos, player);
    }

    public boolean attackUse(BlockPos pos, Vec3 location, Player player, ItemStack stack) {
        if (player.level().isClientSide) {
            VeilPacketManager.server().sendPacket(new PlayerAttackUseC2SPacket(pos, location));
        }
        if (player.level().getBlockEntity(pos) instanceof PanelBlockEntity panel) {
            panel.removeWidget(location);
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().getBlockState(pContext.getClickedPos()).is(SuperpositionBlocks.ANTENNA.get())) {
            BlockState state = pContext.getLevel().getBlockState(pContext.getClickedPos());
            BlockState stateUp = pContext.getLevel().getBlockState(pContext.getClickedPos().above());
            BlockState stateDown = pContext.getLevel().getBlockState(pContext.getClickedPos().below());
            if (stateUp.is(SuperpositionBlocks.ANTENNA.get()))
                return InteractionResult.FAIL;
            if (stateDown.is(SuperpositionBlocks.ANTENNA.get()))
                return InteractionResult.FAIL;
            pContext.getLevel().setBlock(pContext.getClickedPos(), pContext.getLevel().getBlockState(pContext.getClickedPos()).setValue(AntennaBlock.SHORT, !state.getValue(AntennaBlock.SHORT)), 2);
            if (pContext.getLevel().isClientSide)
                return InteractionResult.SUCCESS;
        }
        if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof SignalActorBlockEntity signalActorBlockEntity) {
            if (pContext.getPlayer().isShiftKeyDown())
                signalActorBlockEntity.incrementConfigSelection();
            else
                signalActorBlockEntity.interactConfig();
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }
}
