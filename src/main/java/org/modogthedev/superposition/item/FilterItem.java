package org.modogthedev.superposition.item;

import net.minecraft.world.item.Item;

public class FilterItem extends Item {
    public FilterType type;
    public FilterItem(Properties pProperties, Item.Properties properties) {
        super(properties);
        type = pProperties.type;
    }

    public static class Properties {
        public FilterType type;
        public FilterItem.Properties type(FilterType type) {
            this.type = type;
            return this;
        }
    }
    public enum FilterType {
        LOW_PASS,
        HIGH_PASS,
        BAND_PASS
    }
}
