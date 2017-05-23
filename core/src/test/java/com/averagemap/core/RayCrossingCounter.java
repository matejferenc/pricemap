package com.averagemap.core;

import org.geojson.LngLatAlt;
import org.locationtech.jts.algorithm.RobustDeterminant;

import java.util.List;

public class RayCrossingCounter {

    public final static int INTERIOR = 0;
    public final static int BOUNDARY = 1;
    public final static int EXTERIOR = 2;

    public static int locatePointInRing(LngLatAlt p, List<LngLatAlt> ring) {
        RayCrossingCounter counter = new RayCrossingCounter(p);

        LngLatAlt p1;
        LngLatAlt p2;
        for (int i = 1; i < ring.size(); i++) {
            p1 = ring.get(i);
            p2 = ring.get(i - 1);
            counter.countSegment(p1, p2);
            if (counter.isOnSegment())
                return counter.getLocation();
        }
        return counter.getLocation();
    }

    private LngLatAlt p;
    private int crossingCount = 0;
    // true if the test point lies on an input segment
    private boolean isPointOnSegment = false;

    public RayCrossingCounter(LngLatAlt p) {
        this.p = p;
    }

    public void countSegment(LngLatAlt p1, LngLatAlt p2) {
        /*
         * For each segment, check if it crosses
         * a horizontal ray running from the test point in the positive x direction.
         */

        // check if the segment is strictly to the left of the test point
        if (p1.getLongitude() < p.getLongitude() && p2.getLongitude() < p.getLongitude())
            return;

        // check if the point is equal to the current ring vertex
        if (p.getLongitude() == p2.getLongitude() && p.getLatitude() == p2.getLatitude()) {
            isPointOnSegment = true;
            return;
        }
        /*
         * For horizontal segments, check if the point is on the segment.
         * Otherwise, horizontal segments are not counted.
         */
        if (p1.getLatitude() == p.getLatitude() && p2.getLatitude() == p.getLatitude()) {
            double minx = p1.getLongitude();
            double maxx = p2.getLongitude();
            if (minx > maxx) {
                minx = p2.getLongitude();
                maxx = p1.getLongitude();
            }
            if (p.getLongitude() >= minx && p.getLongitude() <= maxx) {
                isPointOnSegment = true;
            }
            return;
        }
        /*
         * Evaluate all non-horizontal segments which cross a horizontal ray to the
         * right of the test pt. To avoid double-counting shared vertices, we use the
         * convention that
         * <ul>
         * <li>an upward edge includes its starting endpoint, and excludes its
         * final endpoint
         * <li>a downward edge excludes its starting endpoint, and includes its
         * final endpoint
         * </ul>
         */
        if (((p1.getLatitude() > p.getLatitude()) && (p2.getLatitude() <= p.getLatitude()))
                || ((p2.getLatitude() > p.getLatitude()) && (p1.getLatitude() <= p.getLatitude()))) {
            // translate the segment so that the test point lies on the origin
            double x1 = p1.getLongitude() - p.getLongitude();
            double y1 = p1.getLatitude() - p.getLatitude();
            double x2 = p2.getLongitude() - p.getLongitude();
            double y2 = p2.getLatitude() - p.getLatitude();

            /*
             * The translated segment straddles the x-axis. Compute the sign of the
             * ordinate of intersection with the x-axis. (y2 != y1, so denominator
             * will never be 0.0)
             */
            // double xIntSign = RobustDeterminant.signOfDet2x2(x1, y1, x2, y2) / (y2
            // - y1);
            // MD - faster & more robust computation?
            double xIntSign = RobustDeterminant.signOfDet2x2(x1, y1, x2, y2);
            if (xIntSign == 0.0) {
                isPointOnSegment = true;
                return;
            }
            if (y2 < y1)
                xIntSign = -xIntSign;
            // xsave = xInt;

            //System.out.println("xIntSign(" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + " = " + xIntSign);
            // The segment crosses the ray if the sign is strictly positive.
            if (xIntSign > 0.0) {
                crossingCount++;
            }
        }
    }

    public boolean isOnSegment() {
        return isPointOnSegment;
    }

    public int getLocation() {
        if (isPointOnSegment)
            return BOUNDARY;

        // The point is in the interior of the ring if the number of X-crossings is
        // odd.
        if ((crossingCount % 2) == 1) {
            return INTERIOR;
        }
        return EXTERIOR;
    }

}
