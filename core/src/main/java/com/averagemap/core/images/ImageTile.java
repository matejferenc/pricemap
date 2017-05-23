package com.averagemap.core.images;

import com.averagemap.core.coordinates.model.GoogleMapsTile;

import java.awt.image.BufferedImage;

public class ImageTile {

    private final GoogleMapsTile tile;

    private final BufferedImage image;

    public ImageTile(GoogleMapsTile tile, BufferedImage image) {
        this.tile = tile;
        this.image = image;
    }

    public GoogleMapsTile getTile() {
        return tile;
    }

    public BufferedImage getImage() {
        return image;
    }
}
