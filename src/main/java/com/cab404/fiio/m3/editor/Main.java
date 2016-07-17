package com.cab404.fiio.m3.editor;

import java.io.File;

/**
 * @author cab404
 */
public class Main {

    public static void main(String[] args) {
        MainScreen screen = new MainScreen();
        screen.show();
        if (args.length > 0) screen.onFileSelected(new File(args[0]));
    }

}
