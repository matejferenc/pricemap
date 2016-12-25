package com.averagemap.core.images;

import java.util.ArrayList;
import java.util.List;

public class ImageTilesForEveryZoom {

    private final List<ImageTilesForOneZoom> oneZoomTilesList;

    public ImageTilesForEveryZoom() {
        oneZoomTilesList = new ArrayList<>();
    }

    public void addImageTiles(ImageTilesForOneZoom imageTilesForOneZoom) {
        int zoom = imageTilesForOneZoom.getZoom();
        oneZoomTilesList.stream()
                .filter(tiles -> tiles.getZoom() == zoom)
                .findAny()
                .ifPresent(tile -> {
                    throw new IllegalArgumentException("already contains images for zoom " + zoom);
                });
        oneZoomTilesList.add(imageTilesForOneZoom);
    }

    public List<ImageTilesForOneZoom> getOneZoomTilesList() {
        return oneZoomTilesList;
    }
}
