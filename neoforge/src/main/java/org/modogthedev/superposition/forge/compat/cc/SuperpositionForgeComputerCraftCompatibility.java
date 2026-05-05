package org.modogthedev.superposition.forge.compat.cc;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.modogthedev.superposition.compat.cc.PortInterfacePeripheral;
import org.modogthedev.superposition.util.PortBehavior;

public class SuperpositionForgeComputerCraftCompatibility {

    public static void attachPeripherals(RegisterCapabilitiesEvent event) {
        for (BlockEntityType<?> blockEntityType : BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            event.registerBlockEntity(PeripheralCapability.get(),blockEntityType,(be, direction) -> {
                if (be instanceof PortBehavior portBehavior) {
                    return new PortInterfacePeripheral(portBehavior);
                }
                return null;
            });
        }
    }
}
