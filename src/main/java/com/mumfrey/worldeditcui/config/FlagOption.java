package com.mumfrey.worldeditcui.config;

import net.minecraft.text.KeybindText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import javax.annotation.Nonnull;

import static com.mumfrey.worldeditcui.WorldEditCUI.tr;
import static java.util.Objects.requireNonNull;

public enum FlagOption implements Option<Boolean> {
    DEBUG_MODE("debugMode", false),
    IGNORE_UPDATES("ignoreUpdates", false),
    PROMISCUOUS("promiscuous", "compat.spammy", false),
    ALWAYS_ON_TOP("alwaysOnTop", "compat.ontop", false),
    CLEAR_ALL_ON_KEY("clearAllOnKey", "extra.clearall", false) {
        @Override
        public Text label() {
            return tr("options." + this.translationKey, new KeybindText("key.worldeditcui.toggle").formatted(Formatting.GREEN));
        }
    };

    // DO NOT MODIFY
    /* package */ static final FlagOption[] VALUES = values();

    private final String key;
    protected final String translationKey;
    private final boolean def;
    private volatile boolean value;

    FlagOption(final String key, final boolean def) {
        this(key, key, def);
    }

    FlagOption(final String key, final String translationKey, final boolean def) {
        this.key = requireNonNull(key, "key");
        this.translationKey = requireNonNull(translationKey, "translationKey");
        this.def = def;
        this.value = def;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Class<Boolean> type() {
        return boolean.class;
    }

    @Override
    public @Nonnull Boolean defaultValue() {
        return this.def;
    }

    @Override
    public Boolean value() {
        return this.value;
    }

    @Override
    public void value(Boolean value) {
        this.value = requireNonNull(value, "value");
    }

    public void value(final boolean value) {
        this.value = value;
    }

    @Override
    public Text label() {
        return tr("options." + this.translationKey);
    }


}
