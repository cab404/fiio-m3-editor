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
import java.util.stream.Collectors;

/**
 * @author cab404
 */
public class MainScreen implements ActionListener, PlaylistChooserDialog.OnFileSelectedListener {
    private JFrame frame;
    private MainScreenForm form;
    private DBTableModel dbModel = new DBTableModel();
    private DBTableModel playlistModel = new DBTableModel();
    private File currentFile;

    public MainScreen() {

        form = new MainScreenForm();
        frame = new JFrame("M3 Playlist Editor");

        JMenuBar menuBar = new JMenuBar();
        {
            JMenu file = new JMenu("File");
            {
                file.setMnemonic('F');

                JMenuItem load = new JMenuItem("Load playlist...");
                {
                    load.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
                    load.setActionCommand("load");
                    load.addActionListener(this);
                    file.add(load);
                }

                JMenuItem save = new JMenuItem("Save playlist");
                {
                    save.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
                    save.setActionCommand("save");
                    save.addActionListener(this);
                    file.add(save);
                }

                menuBar.add(file);
            }
            JMenu edit = new JMenu("Edit");
            {
                edit.setMnemonic('E');

                JMenuItem add = new JMenuItem("Add to playlist");
                {
                    add.setActionCommand("add-songs");
                    add.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
                    add.addActionListener(this);
                    edit.add(add);
                }

                JMenuItem remove = new JMenuItem("Remove from playlist");
                {
                    remove.setActionCommand("remove-songs");
                    remove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                    remove.addActionListener(this);
                    edit.add(remove);
                }

                JMenuItem moveUp = new JMenuItem("Move up in playlist");
                {
                    moveUp.setActionCommand("move-up");
                    moveUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK));
                    moveUp.addActionListener(this);
                    edit.add(moveUp);
                }

                JMenuItem moveDown = new JMenuItem("Move down in playlist");
                {
                    moveDown.setActionCommand("move-down");
                    moveDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK));
                    moveDown.addActionListener(this);
                    edit.add(moveDown);
                }
            }
            menuBar.add(edit);
        }

        frame.setJMenuBar(menuBar);
        frame.setContentPane(form.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        form.dbTable.setModel(dbModel);
        {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem add = new JMenuItem("Add to playlist");
            add.setActionCommand("add-songs");
            menu.add(add);
            add.addActionListener(this);
            form.dbTable.setComponentPopupMenu(menu);
        }

        form.playlistTable.setModel(playlistModel);
        {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem remove = new JMenuItem("Remove from playlist");
            remove.setActionCommand("remove-songs");
            remove.addActionListener(this);
            menu.add(remove);
            form.playlistTable.setComponentPopupMenu(menu);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int[] rows = form.playlistTable.getSelectedRows();
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
                for (int index : form.dbTable.getSelectedRows()) {
                    playlistModel.add(dbModel.getSongs().get(index));
                }
                setStatus("Songs(s) added. " + playlistModel.getSongs().size() + "/100 songs in playlist");
                break;
            case "remove-songs":
                playlistModel.remove(rows);
                setStatus("Songs(s) deleted. " + playlistModel.getSongs().size() + "/100 songs in playlist");
                break;
            case "move-up": {
                for (int row : rows)
                    if (row == 0)
                        return;

                Set<Integer> newSelected = playlistModel.moveRows(-1, rows);
                form.playlistTable.clearSelection();
                for (int row : newSelected)
                    form.playlistTable.addRowSelectionInterval(row, row);
                break;
            }
            case "move-down": {
                for (int row : rows)
                    if (row == playlistModel.getSongs().size() - 1)
                        return;
                Set<Integer> newSelected = playlistModel.moveRows(1, rows);
                form.playlistTable.clearSelection();
                for (int row : newSelected)
                    form.playlistTable.addRowSelectionInterval(row, row);
                break;
            }
        }
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
            form.status.setText("Playlist save failed: " + e.getMessage());
            return;
        }
        form.status.setText("Playlist saved.");
    }

    public void show() {
        frame.setVisible(true);
    }

    public void setDB(List<Song> songs) {
        dbModel.setSongs(songs);
    }

    public void setPlaylist(List<Song> playlist) {
        playlistModel.setSongs(playlist);
    }

    public void setStatus(String status) {
        form.status.setText(status);
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
            form.status.setText("DB load failed: " + e.getMessage());
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
            form.status.setText("Playlist load failed: " + e.getMessage());
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
