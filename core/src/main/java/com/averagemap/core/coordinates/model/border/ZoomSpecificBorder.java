package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.CoordinatesUtils;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.TilesArea;

import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public class ZoomSpecificBorder {

    private final MultiPolygon<GoogleMapsPosition> zoomSpecificMultiPolygon;

    private PathMultiPolygon pathMultiPolygon;

    public ZoomSpecificBorder(MultiPolygon<GoogleMapsPosition> zoomSpecificMultiPolygon) {
        this.zoomSpecificMultiPolygon = zoomSpecificMultiPolygon;
    }

    public TilesArea getEncompassingArea() {
        final int zoom = assertZoomLevelIsTheSame(zoomSpecificMultiPolygon);
        int left = MAX_VALUE;
        int top = MAX_VALUE;
        int right = MIN_VALUE;
        int bottom = MIN_VALUE;
        for (Polygon<GoogleMapsPosition> polygon : zoomSpecificMultiPolygon.getPolygons()) {
            for (GoogleMapsPosition borderPoint : polygon.getExteriorRing().getLineString()) {
                int x = borderPoint.getX();
                if (x < left) {
                    left = x;
                } else if (x > right) {
                    right = x;
                }
                int y = borderPoint.getY();
                if (y < top) {
                    top = y;
                } else if (y > bottom) {
                    bottom = y;
                }
            }
        }
        return new TilesArea(
                CoordinatesUtils.positionToTile(new GoogleMapsPosition(left, top, zoom)),
                CoordinatesUtils.positionToTile(new GoogleMapsPosition(right, bottom, zoom)));
    }

    private int assertZoomLevelIsTheSame(MultiPolygon<GoogleMapsPosition> border) {
        Integer zoom = null;
        for (Polygon<GoogleMapsPosition> polygon : border.getPolygons()) {
            for (GoogleMapsPosition borderPoint : polygon.getExteriorRing().getLineString()) {
                if (zoom == null) {
                    zoom = borderPoint.getZoom();
                } else {
                    if (zoom != borderPoint.getZoom()) {
                        throw new IllegalArgumentException("zoom levels different");
                    }
                }
            }
        }
        if (zoom == null ){
            throw new IllegalArgumentException("empty");
        }
        return zoom;
    }

    public void prepareForPlotting() {
        pathMultiPolygon = new PathMultiPolygon();
        for (Polygon<GoogleMapsPosition> polygon : zoomSpecificMultiPolygon.getPolygons()) {
            PathPolygon pathPolygon = new PathPolygon();
            pathPolygon.exteriorRing = createPathFromRing(polygon.getExteriorRing());
            polygon.getHoles().forEach(hole -> pathPolygon.holes.add(createPathFromRing(hole)));
            pathMultiPolygon.pathPolygons.add(pathPolygon);
        }
    }

    private GeneralPath createPathFromRing(LinearRing<GoogleMapsPosition> linearRing) {
        List<GoogleMapsPosition> lineString = linearRing.getLineString();
        GeneralPath clip = new GeneralPath(Path2D.WIND_EVEN_ODD);
        GoogleMapsPosition first = lineString.get(1);//first and last point are the same
        clip.moveTo(first.getX(), first.getY());
        lineString.forEach(position -> clip.lineTo(position.getX(), position.getY()));
        clip.closePath();
        return clip;
    }

    public BorderInTile cropToTile(GoogleMapsTile tile) {
        Rectangle2D.Double tileRectangle = new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        for (PathPolygon pathPolygon : pathMultiPolygon.pathPolygons) {
            if (pathPolygon.exteriorRing.intersects(tileRectangle) || pathPolygon.exteriorRing.contains(tileRectangle)) {
                Area area = new Area(pathPolygon.exteriorRing);
                area.intersect(new Area(tileRectangle));
            }
        }
    }

    private static class PathMultiPolygon {
        private List<PathPolygon> pathPolygons = new ArrayList<>();
    }

    private static class PathPolygon {
        private GeneralPath exteriorRing;
        private List<GeneralPath> holes = new ArrayList<>();
    }
}
