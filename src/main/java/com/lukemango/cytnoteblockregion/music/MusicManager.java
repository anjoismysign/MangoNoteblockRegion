package com.lukemango.cytnoteblockregion.music;

import com.lukemango.cytnoteblockregion.CYTNoteblockRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {

    private final CYTNoteblockRegion plugin;

    private final Map<String, Song> songs = new HashMap<>();
    private final Map<ProtectedRegion, RadioSongPlayer> regionSongs = new HashMap<>();

    public MusicManager(CYTNoteblockRegion plugin) {
        this.plugin = plugin;
        MusicRegister musicRegister = new MusicRegister(this);

        musicRegister.loadSongs();
        musicRegister.loadRegions();

        plugin.getWorldGuardUtil().startCheckingPlayers();
    }

    public CYTNoteblockRegion getPlugin() {
        return plugin;
    }

    public Map<String, Song> getSongs() {
        return songs;
    }

    public void addSong(String title, Song song) {
        songs.put(title, song);
    }

    public Map<ProtectedRegion, RadioSongPlayer> getRegionSongs() {
        return regionSongs;
    }
}
