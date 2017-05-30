package com.averagemap.core;

import com.averagemap.core.coordinates.model.GoogleMapsTile;
import com.averagemap.core.coordinates.model.Position2D;
import org.junit.Test;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;

public class AreaTest {

    @Test
    public void testAreaContainsItself() {
        GoogleMapsTile tile = new GoogleMapsTile(345, 574, 8);
        Rectangle2D.Double tileRectangle = new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        Area area = new Area(tileRectangle);
        Rectangle2D.Double theSameRectangle = new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        boolean contains = area.contains(theSameRectangle);
        System.out.println("contains: " + contains);
        boolean intersects = area.intersects(theSameRectangle);
        System.out.println("intersects: " + intersects);
        boolean equals = area.equals(new Area(theSameRectangle));
        System.out.println("equals: " + equals);
    }

    @Test
    public void testAreaContainsAnotherArea() {
        GoogleMapsTile tile = new GoogleMapsTile(345, 574, 8);
        Rectangle2D.Double tileRectangle = new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        Area area = new Area(tileRectangle);
        Rectangle2D.Double halfRectangle = new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE / 2);
        boolean contains = area.contains(halfRectangle);
        System.out.println("contains: " + contains);
        boolean intersects = area.intersects(halfRectangle);
        System.out.println("intersects: " + intersects);
        boolean equals = area.equals(new Area(halfRectangle));
        System.out.println("equals: " + equals);
    }
}
