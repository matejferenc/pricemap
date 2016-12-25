package com.averagemap.core.images;

import java.util.Collection;

public class ImageTilesForOneZoom {

    private final Collection<ImageTile> tiles;

    private final int zoom;

    public ImageTilesForOneZoom(Collection<ImageTile> tiles, int zoom) {
        this.tiles = tiles;
        this.zoom = zoom;
    }

    public Collection<ImageTile> getTiles() {
        return tiles;
    }

    public int getZoom() {
        return zoom;
    }
}
