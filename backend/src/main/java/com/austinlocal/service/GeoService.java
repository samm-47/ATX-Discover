package com.austinlocal.service;

import org.springframework.stereotype.Service;

/**
 * Pure-math geo distance calculation using the Haversine formula.
 * No external geo API or paid service required — this is the same
 * math a PostGIS ST_Distance call approximates for short distances,
 * just computed in-process.
 */
@Service
public class GeoService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Returns the great-circle distance between two lat/long points, in kilometers.
     */
    public double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
