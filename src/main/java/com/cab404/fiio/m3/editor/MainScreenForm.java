package com.cab404.fiio.m3.editor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * @author cab404
 */
public class MainScreenForm {
    public JPanel root;
    public JTable dbTable;
    public JTable playlistTable;
    public JLabel status;

    public MainScreenForm(){
        root = new JPanel();
        JSplitPane splitPane = new JSplitPane();
        root.add(splitPane);
        {
            JScrollPane pane = new JScrollPane(dbTable = new JTable());
            pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(2), "Database"));
            splitPane.add(pane, JSplitPane.LEFT);
        }
            JScrollPane pane = new JScrollPane(playlistTable = new JTable());
            pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(2), "Playlist"));{
            splitPane.add(pane, JSplitPane.RIGHT);
        }
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);

        root.add(status = new JLabel("Fiio M3 playlist editor by cab404"));
        status.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));

        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
    }

}
