package com.ruse.util;

import java.io.File;
import java.io.IOException;

/**
 * File utilities.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class FileUtils {

    /**
     * Creates a new file and the directory it exists in.
     * @param directory the directory
     * @param file the file
     * @throws IOException if the file cannot be created
     */
    public static void createNewFile(String directory, String file) throws IOException {
        createNewFile(directory + file);
    }

    /**
     * Creates a new file, will not create the directory it exists in.
     * @param file the file path
     * @throws IOException if the file cannot be created.
     */
    public static void createNewFile(String file) throws IOException {
        File f = new File(file);
        if (!f.exists() && !f.createNewFile()) {
            throw new IllegalStateException();
        }
    }

    /**
     * Creates all the default save folders.
     */
    public static void createSaveDirectories() {
        String dir = "./data/saves/";
        String[] dirs = {
                "characters",
                "clans",
                "exchange",
                "farming",
                "housing",
                "housing/furniture",
                "housing/portals",
                "housing/rooms",
                "logs",
                "lottery",
                "sanctions",
        };

        File dirFile = new File(dir);
        if (!dirFile.exists() && !dirFile.mkdirs())
            throw new IllegalStateException();
        for (String directory : dirs) {
            dirFile = new File(dir + directory);
            if (!dirFile.exists() && !dirFile.mkdirs()) {
                throw new IllegalStateException();
            }
        }
    }

}
