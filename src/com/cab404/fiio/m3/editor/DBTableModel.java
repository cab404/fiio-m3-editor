package com.cab404.fiio.m3.editor;

import com.cab404.fiio.m3.db.data.Song;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cab404
 */
public class DBTableModel extends AbstractTableModel {

    private List<Song> songs = new ArrayList<>();

    private static final String[] columnNames = {"Track", "Name", "Album", "Author", "Genre"};

    public List<Song> getSongs() {
        return songs;
    }

    public void add(Song song){
        songs.add(song);
        fireTableRowsInserted(songs.size() - 1, songs.size() - 1);
    }

    public void remove(int[] rows){
        List<Integer> rowsSorted = new ArrayList<>();
        for (int row : rows) rowsSorted.add(row);
        Collections.sort(rowsSorted);
        Collections.reverse(rowsSorted);

        for (Integer index : rowsSorted) {
            songs.remove((int) index);
            fireTableRowsDeleted(index, index);
        }
    }

    @Override
    public int getRowCount() {
        return songs.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int i) {
        return columnNames[i];
    }

    @Override
    public Class<?> getColumnClass(int i) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Song song = songs.get(row);
        if (song == null) return null;
        switch (col){
            case 0: return song.index;
            case 1: return song.name;
            case 2: return song.album;
            case 3: return song.author;
            case 4: return song.genre;
            default: return null;
        }
    }

    @Override
    public void setValueAt(Object o, int i, int i1) {}

    public void setSongs(List<Song> data) {
        this.songs = data;
        fireTableDataChanged();
    }

    public Set<Integer> moveRows(int by, int[] rows) {
        List<Integer> rowsSorted = new ArrayList<>();
        for (int row : rows) rowsSorted.add(row);
        Collections.sort(rowsSorted);
        if (by > 0) Collections.reverse(rowsSorted);

        Set<Integer> newRows = new HashSet<>();
        for (Integer row : rowsSorted) {

            Song song = songs.remove((int) row);

            row += by;
            if (row < 0) row = 0;
            if (row > songs.size()) row = songs.size();
            songs.add(row, song);

            newRows.add(row);
        }

        fireTableDataChanged();
        return newRows;
    }
}
