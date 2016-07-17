package com.cab404.fiio.m3.editor;

import com.cab404.fiio.m3.db.M3Library;
import com.cab404.fiio.m3.db.M3Playlist;
import com.cab404.fiio.m3.db.data.PlaylistEntry;
import com.cab404.fiio.m3.db.data.Song;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cab404
 */
public class MainScreen implements ActionListener, PlaylistChooserDialog.OnFileSelectedListener {
    private MainScreenFrame frame;
    private DBTableModel dbModel = new DBTableModel();
    private DBTableModel playlistModel = new DBTableModel();
    private File currentFile;

    public MainScreen() {

        frame = new MainScreenFrame(this);
        frame.dbTable.setModel(dbModel);
        frame.playlistTable.setModel(playlistModel);
        frame.playlistTable.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.isAltDown()) {
                    if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN)
                        movePlaylistRows(1);
                    if (keyEvent.getKeyCode() == KeyEvent.VK_UP)
                        movePlaylistRows(-1);
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {}
        });

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        long time = System.currentTimeMillis();
        switch (actionEvent.getActionCommand()) {
            case "save":
                if (currentFile != null)
                    save();
                break;
            case "load":
                PlaylistChooserDialog dialog = new PlaylistChooserDialog(frame);
                dialog.setFileSelectedListener(this);
                if (currentFile != null)
                    dialog.fileChooser.setCurrentDirectory(currentFile.getParentFile());
                dialog.show();
                break;
            case "add-songs":
                for (int index : frame.dbTable.getSelectedRows()) {
                    playlistModel.add(dbModel.getSongs().get(index));
                }
                setStatus("Songs(s) added. " + playlistModel.getSongs().size() + "/100 songs in playlist");
                break;
            case "remove-songs":
                playlistModel.remove(frame.playlistTable.getSelectedRows());
                setStatus("Songs(s) deleted. " + playlistModel.getSongs().size() + "/100 songs in playlist");
                break;
        }

        System.out.println("took " + (System.currentTimeMillis() - time) + " ms");
    }

    private void movePlaylistRows(int by) {
        int[] rows = frame.playlistTable.getSelectedRows();
        int size = playlistModel.getSongs().size() - 1;
        for (int row : rows)
            if (row == (by > 0 ? size : 0))
                return;

        Set<Integer> newSelected = playlistModel.moveRows(by, rows);
        frame.playlistTable.clearSelection();
        for (int row : newSelected)
            frame.playlistTable.addRowSelectionInterval(row, row);
    }

    private void save() {
        List<Song> songs = playlistModel.getSongs();
        if (songs.size() > 100) {
            setStatus("Playlist cannot hold more than 100 songs!");
            return;
        }
        if (songs.size() == 0) {
            setStatus("Playlists cannot be erased at the moment!");
            return;
        }
        try {
            M3Playlist.rewritePlaylist(currentFile, songs);
        } catch (IOException e) {
            e.printStackTrace();
            frame.status.setText("Playlist save failed: " + e.getMessage());
            return;
        }
        frame.status.setText("Playlist saved.");
    }

    public void show() {
        frame.setVisible(true);
    }

    private void setDB(List<Song> songs) {
        dbModel.setSongs(songs);
    }

    private void setPlaylist(List<Song> playlist) {
        playlistModel.setSongs(playlist);
    }

    private void setStatus(String status) {
        frame.status.setText(status);
    }

    @Override
    public void onFileSelected(File file) {
        currentFile = file;
        File dbFile = new File(file.getParentFile(), "MUSIC.LIB");
        setStatus("Loading...");

        List<Song> db;
        try {
            db = M3Library.readDB(dbFile);
        } catch (IOException e) {
            e.printStackTrace();
            frame.status.setText("DB load failed: " + e.getMessage());
            return;
        }

        HashMap<Integer, Song> mappedDb = new HashMap<>();
        for (Song song : db) {
            mappedDb.put(Arrays.hashCode(song.file), song);
        }

        List<PlaylistEntry> entries;
        try {
            entries = M3Playlist.readPL(file);
        } catch (IOException e) {
            e.printStackTrace();
            frame.status.setText("Playlist load failed: " + e.getMessage());
            return;
        }

        List<Song> playlistSongs =
                entries
                        .stream()
                        .map(entry -> mappedDb.get(Arrays.hashCode(entry.song.file)))
                        .collect(Collectors.toList());
        playlistSongs
                .removeIf(a -> a == null);

        if (playlistSongs.isEmpty()) {
            setStatus("Cannot edit empty playlists, please add something in player UI and retry.");
            return;
        }

        setDB(db);
        setPlaylist(playlistSongs);

        setStatus("Loaded " + file);
    }
}
