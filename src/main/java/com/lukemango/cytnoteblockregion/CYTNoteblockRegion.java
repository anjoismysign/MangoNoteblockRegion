package com.lukemango.cytnoteblockregion;

import com.lukemango.cytnoteblockregion.music.MusicManager;
import com.lukemango.cytnoteblockregion.utils.WorldGuardUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class CYTNoteblockRegion extends JavaPlugin {

    private MusicManager musicManager;
    private WorldGuardUtil worldGuardUtil;

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        worldGuardUtil = new WorldGuardUtil(this);
        musicManager = new MusicManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        worldGuardUtil.cancelTask();
        worldGuardUtil.stopPlaying();
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public WorldGuardUtil getWorldGuardUtil() {
        return worldGuardUtil;
    }
}


