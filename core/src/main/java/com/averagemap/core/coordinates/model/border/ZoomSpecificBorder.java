package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.TilesArea;

public interface ZoomSpecificBorder {

    TilesArea getEncompassingArea();

    void prepareForPlotting();

    BorderInTile cropToTile(GoogleMapsTile tile);

}
