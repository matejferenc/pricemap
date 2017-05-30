package com.averagemap.core.coordinates.model.border;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

public class PolygonInTile {

    private Area exteriorRing;

    private List<Area> holes = new ArrayList<>();

    public PolygonInTile(Area exteriorRing, List<Area> holes) {
        this.exteriorRing = exteriorRing;
        this.holes = holes;
    }

    public Area getExteriorRing() {
        return exteriorRing;
    }

    public void setExteriorRing(Area exteriorRing) {
        this.exteriorRing = exteriorRing;
    }

    public List<Area> getHoles() {
        return holes;
    }

    public void setHoles(List<Area> holes) {
        this.holes = holes;
    }
}
