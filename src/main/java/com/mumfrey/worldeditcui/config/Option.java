package com.mumfrey.worldeditcui.config;

import net.minecraft.text.Text;

import javax.annotation.Nullable;

/**
 * A type of option
 */
public interface Option<V> {

    /**
     * Key to store this option as in the configuration
     *
     * @return config key
     */
    String key();

    /**
     * Value type to store as
     *
     * @return value type
     */
    Class<V> type();

    /**
     * The option's default value, if any.
     *
     * @return default
     */
    V defaultValue();

    /**
     * The option's current value
     *
     * @return value
     */
    V value();

    /**
     * Set the option's value
     *
     * @param value new value
     */
    void value(final V value);

    /**
     * Label for the config gui.
     *
     * @return Config gui label
     */
    Text label();

}
