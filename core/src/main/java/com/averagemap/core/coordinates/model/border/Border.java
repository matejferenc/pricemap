package com.averagemap.core.coordinates.model.border;

import com.averagemap.core.coordinates.CoordinatesUtils;
import com.averagemap.core.coordinates.model.GoogleMapsPosition;
import com.averagemap.core.coordinates.model.LatLng;

import static java.util.stream.Collectors.toList;

public class Border {

    private final MultiPolygon<LatLng> multiPolygon;

    private final ZoomSpecificBorderFactory zoomSpecificBorderFactory;

    public Border(MultiPolygon<LatLng> multiPolygon, ZoomSpecificBorderFactory zoomSpecificBorderFactory) {
        this.multiPolygon = multiPolygon;
        this.zoomSpecificBorderFactory = zoomSpecificBorderFactory;
    }

    public ZoomSpecificBorder createForZoom(int zoom) {
        return zoomSpecificBorderFactory.createZoomSpecificBorder(removeDuplicatePositions(latLngToPosition(multiPolygon, zoom)));
    }

    private MultiPolygon<GoogleMapsPosition> removeDuplicatePositions(MultiPolygon<GoogleMapsPosition> multiPolygon) {
        return new MultiPolygon<>(multiPolygon.getPolygons().stream()
                .map(polygon -> new Polygon<>(removeDuplicatePositions(polygon.getExteriorRing()),
                        polygon.getHoles().stream()
                                .map(this::removeDuplicatePositions)
                                .filter(hole -> hole.getLineString().size() >= 3)
                                .collect(toList())))
                .filter(polygon -> polygon.getExteriorRing().getLineString().size() >= 3)
                .collect(toList()));
    }

    private LinearRing<GoogleMapsPosition> removeDuplicatePositions(LinearRing<GoogleMapsPosition> ring) {
        return new LinearRing<>(ring.getLineString().stream()
                .distinct()
                .collect(toList()));
    }

    private MultiPolygon<GoogleMapsPosition> latLngToPosition(MultiPolygon<LatLng> multiPolygon, int zoom) {
        return new MultiPolygon<>(multiPolygon.getPolygons().stream()
                .map(polygon -> new Polygon<>(latLngToPosition(polygon.getExteriorRing(), zoom),
                        polygon.getHoles().stream()
                                .map(hole -> latLngToPosition(hole, zoom))
                                .collect(toList()))
                )
                .collect(toList()));
    }

    private LinearRing<GoogleMapsPosition> latLngToPosition(LinearRing<LatLng> ring, int zoom) {
        return new LinearRing<>(
                ring.getLineString().stream()
                        .map(latLng -> CoordinatesUtils.latLngToPosition(latLng, zoom))
                        .collect(toList()));
    }

}
