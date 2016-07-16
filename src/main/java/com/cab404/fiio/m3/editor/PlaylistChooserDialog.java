package com.cab404.fiio.m3.editor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author cab404
 */
public class PlaylistChooserDialog implements ActionListener {

    JDialog dialog;
    JFileChooser fileChooser;

    public PlaylistChooserDialog(JFrame frame) {
        dialog = new JDialog(frame, "Choose playlist", true);
        fileChooser = new JFileChooser(new File("."));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fiio M3 playlist files", "pl"));
        fileChooser.addActionListener(this);
        dialog.setContentPane(fileChooser);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
    }

    public void show(){
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (JFileChooser.CANCEL_SELECTION.equals(actionEvent.getActionCommand())){
            dialog.dispose();
        }
        if (JFileChooser.APPROVE_SELECTION.equals(actionEvent.getActionCommand())){
            if (fileSelectedListener != null)
                fileSelectedListener.onFileSelected(fileChooser.getSelectedFile());
            dialog.dispose();
        }
    }


    public interface OnFileSelectedListener{
        void onFileSelected(File file);
    }

    OnFileSelectedListener fileSelectedListener;

    public void setFileSelectedListener(OnFileSelectedListener fileSelectedListener) {
        this.fileSelectedListener = fileSelectedListener;
    }

}
