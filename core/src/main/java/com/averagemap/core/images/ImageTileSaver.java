package com.averagemap.core.images;

import com.averagemap.core.coordinates.GoogleMapsTile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.io.File.separator;

public class ImageTileSaver {

    private File rootFolder;

    public ImageTileSaver(File rootFolder) {
        assertFolder(rootFolder);
        this.rootFolder = rootFolder;
    }

    public void saveTile(ImageTile tile) {
        GoogleMapsTile mapsTile = tile.getTile();
        File directory = new File(rootFolder, mapsTile.getZoom() + separator + mapsTile.getX());
        createDirectoryIfNotExists(directory);
        File imageFile = new File(directory, mapsTile.getY().toString() + ".png");
        saveImage(imageFile, tile.getImage());
    }

    private void saveImage(File imageFile, BufferedImage image) {
        try {
            ImageIO.write(image, "PNG", imageFile);
        } catch (IOException e) {
            throw new IllegalStateException("cannot write image to file: " + imageFile.getAbsolutePath(), e);
        }
    }

    private void createDirectoryIfNotExists(File directory) {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IllegalStateException("cannot create folder " + directory.getAbsolutePath());
            }
        }
    }

    private void assertFolder(File rootFolder) {
        if (!rootFolder.exists()) {
            throw new IllegalArgumentException("root folder does not exist");
        }
        if (!rootFolder.isDirectory()) {
            throw new IllegalArgumentException("root folder is not a folder");
        }
        if (!rootFolder.canWrite()) {
            throw new IllegalArgumentException("cannot write to root folder");
        }
    }
}
