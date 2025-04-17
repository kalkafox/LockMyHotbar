package dev.kalkafox.lockmyhotbar.client.config;

import io.wispforest.owo.config.annotation.Config;

@Config(name = "lockmyhotbar-config", wrapperName = "Config")
public class ConfigModel {
    public boolean[] lockedSlots = new boolean[9];

    public boolean notifyPlayer = false;
}
