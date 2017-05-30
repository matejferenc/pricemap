package com.averagemap.core;

import com.averagemap.core.coordinates.model.GoogleMapsTile;
import org.junit.Test;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import static com.averagemap.core.coordinates.CoordinatesUtils.TILE_SIZE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AreaTest {

    @Test
    public void testAreaContainsItself() {
        GoogleMapsTile tile = new GoogleMapsTile(345, 574, 8);
        Rectangle2D.Double tileRectangle = new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        Area area = new Area(tileRectangle);
        Rectangle2D.Double theSameRectangle = new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        assertTrue(area.contains(theSameRectangle));
        assertTrue(area.intersects(theSameRectangle));
        assertTrue(area.equals(new Area(theSameRectangle)));
    }

    @Test
    public void testAreaContainsAnotherArea() {
        GoogleMapsTile tile = new GoogleMapsTile(345, 574, 8);
        Rectangle2D.Double tileRectangle = new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        Area area = new Area(tileRectangle);
        Rectangle2D.Double halfRectangle = new Rectangle2D.Double(tile.getX() * TILE_SIZE, tile.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE / 2);
        assertTrue(area.contains(halfRectangle));
        assertTrue(area.intersects(halfRectangle));
        assertFalse(area.equals(new Area(halfRectangle)));
    }
}
