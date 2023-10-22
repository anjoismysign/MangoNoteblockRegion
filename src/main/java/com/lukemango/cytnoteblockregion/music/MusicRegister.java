package com.lukemango.cytnoteblockregion.music;

import com.lukemango.cytnoteblockregion.CYTNoteblockRegion;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

public class MusicRegister {

    private final MusicManager musicManager;
    private final CYTNoteblockRegion plugin;

    public MusicRegister(MusicManager musicManager) {
        this.musicManager = musicManager;
        this.plugin = musicManager.getPlugin();
    }

    public void loadSongs() {
        plugin.getLogger().log(INFO, "Loading songs...");
        File musicFolder = new File(plugin.getDataFolder(), "music");
        if (!(musicFolder.exists())) {
            musicFolder.mkdir();
        }

        if (musicFolder.listFiles() == null) {
            plugin.getLogger().log(SEVERE, "No music files found!");
        }

        for (File file : musicFolder.listFiles()) {
            if (!file.getName().endsWith(".nbs")) {
                continue;
            }

            Song song = NBSDecoder.parse(file);
            musicManager.addSong(file.getName().replace(".nbs", ""), song);
        }

        plugin.getLogger().log(INFO, "Loaded " + musicManager.getSongs().size() + " songs!");
    }

    public void loadRegions() {
        final FileConfiguration config = plugin.getConfig();
        final Set<String> worldSet = config.getConfigurationSection("regions").getKeys(false);

        plugin.getLogger().log(INFO, "Loading regions...");

        for (String worldName : worldSet) {
            if (Bukkit.getWorld(worldName) == null) {
                plugin.getLogger().warning("World " + worldName + " does not exist!");
                continue;
            }

            final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            final Set<String> regionSet = config.getConfigurationSection("regions." + worldName).getKeys(false);
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("World " + worldName + " does not exist!");
                return;
            }
            com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(world);
            RegionManager regionList = container.get(wgWorld);
            for (String region : regionSet) {
                plugin.getLogger().log(INFO, "Loading region " + region + " in world " + worldName + "...");
                ProtectedRegion rg = regionList.getRegion(region);

                if (rg == null) {
                    plugin.getLogger().warning("Region " + region + " does not exist!");
                    continue;
                }

                final List<String> regionSongs = config.getStringList("regions." + worldName + "." + region + ".songs");
                final List<Song> songs = new ArrayList<>();

                for (String song : regionSongs) {
                    if (musicManager.getSongs().containsKey(song)) {
                        songs.add(musicManager.getSongs().get(song));
                    } else {
                        plugin.getLogger().warning("Song " + song + " does not exist!");
                    }
                }
                final RadioSongPlayer player = new RadioSongPlayer(new Playlist(songs.toArray(new Song[0])));

                if (!musicManager.getRegionSongs().containsKey(rg)) {
                    musicManager.getRegionSongs().put(rg, player);
                } else {
                    musicManager.getRegionSongs().replace(rg, player);
                }

                player.setAutoDestroy(false);
                player.setPlaying(true);
                boolean loop = config.getBoolean("regions." + worldName + "." + region + ".loop");
                player.setRepeatMode(loop ? RepeatMode.ALL : RepeatMode.NO);
                player.setRandom(config.getBoolean("regions." + worldName + "." + region + ".shuffle"));
                player.setVolume((byte) config.getInt("regions." + worldName + "." + region + ".volume"));
                player.setTick((short) config.getInt("regions." + worldName + "." + region + ".tick"));
            }
        }
        plugin.getLogger().log(INFO, "Loaded " + musicManager.getRegionSongs().size() + " regions!");
    }
}
