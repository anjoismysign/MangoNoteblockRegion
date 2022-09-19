package com.lukemango.cytnoteblockregion.utils;

import com.lukemango.cytnoteblockregion.CYTNoteblockRegion;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class WorldGuardUtil {

    CYTNoteblockRegion plugin;
    private final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    private BukkitTask taskID;


    public WorldGuardUtil(CYTNoteblockRegion plugin) {
        this.plugin = plugin;
    }

    public void cancelTask() {
        taskID.cancel();
    }

    public void stopPlaying() {
        for (RadioSongPlayer songPlayer : plugin.getMusicManager().getRegionSongs().values()) {
            songPlayer.setPlaying(false);
        }
    }

    public void startCheckingPlayers() {
        taskID = new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(player.getLocation());

                    RegionQuery query = container.createQuery();
                    ApplicableRegionSet set = query.getApplicableRegions(wgLocation);

                    List<RadioSongPlayer> playerNotIn = new ArrayList<>();

                    if (set.getRegions().isEmpty()) {
                        for (RadioSongPlayer songPlayer : plugin.getMusicManager().getRegionSongs().values()) {
                            songPlayer.removePlayer(player);
                        }
                    }

                    for (ProtectedRegion rg : set) {
                        if (rg == null) {
                            for (RadioSongPlayer songPlayer : plugin.getMusicManager().getRegionSongs().values()) {
                                songPlayer.removePlayer(player);
                            }
                            continue;
                        }

                        playerNotIn.addAll(plugin.getMusicManager().getRegionSongs().values());

                        if (plugin.getMusicManager().getRegionSongs().containsKey(rg)) {
                            RadioSongPlayer songPlayer = plugin.getMusicManager().getRegionSongs().get(rg);
                            songPlayer.addPlayer(player);
                            playerNotIn.remove(songPlayer);
                        }

                        for (RadioSongPlayer songPlayer : playerNotIn) {
                            songPlayer.removePlayer(player);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 10);
    }
}
