package com.cab404.fiio.m3.editor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author cab404
 */
public class MainScreenFrame extends JFrame {
    public JPanel root;
    public JTable dbTable;
    public JTable playlistTable;
    public JLabel status;

    public MainScreenFrame(ActionListener listener) {
        super("M3 Playlist Editor");

        // Setting up main layout
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

        // Setting up menus
        JMenuBar menuBar = new JMenuBar();
        {
            JMenu file = new JMenu("File");
            {
                file.setMnemonic('F');

                JMenuItem load = new JMenuItem("Load playlist...");
                {
                    load.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
                    load.setActionCommand("load");
                    load.addActionListener(listener);
                    file.add(load);
                }

                JMenuItem save = new JMenuItem("Save playlist");
                {
                    save.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
                    save.setActionCommand("save");
                    save.addActionListener(listener);
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
                    add.addActionListener(listener);
                    edit.add(add);
                }

                JMenuItem remove = new JMenuItem("Remove from playlist");
                {
                    remove.setActionCommand("remove-songs");
                    remove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                    remove.addActionListener(listener);
                    edit.add(remove);
                }

                edit.addSeparator();
                JPanel panel = new JPanel();
                TitledBorder border = BorderFactory.createTitledBorder(
                        BorderFactory.createBevelBorder(2),
                        "Available through keystrokes"
                );
                panel.setEnabled(false);
                panel.setBorder(border);
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                edit.add(panel);

                JMenuItem moveUp = new JMenuItem("Move up in playlist");
                {
                    moveUp.setActionCommand("move-up");
                    moveUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK));
                    moveUp.addActionListener(listener);
                    moveUp.setEnabled(false);
                    panel.add(moveUp);
                }

                JMenuItem moveDown = new JMenuItem("Move down in playlist");
                {
                    moveDown.setActionCommand("move-down");
                    moveDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK));
                    moveDown.addActionListener(listener);
                    moveDown.setEnabled(false);
                    panel.add(moveDown);
                }
            }
            menuBar.add(edit);
        }

        {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem add = new JMenuItem("Add to playlist");
            add.setActionCommand("add-songs");
            menu.add(add);
            add.addActionListener(listener);
            dbTable.setComponentPopupMenu(menu);
        }

        {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem remove = new JMenuItem("Remove from playlist");
            remove.setActionCommand("remove-songs");
            remove.addActionListener(listener);
            menu.add(remove);
            playlistTable.setComponentPopupMenu(menu);
        }

        setJMenuBar(menuBar);
        setContentPane(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

    }

}
