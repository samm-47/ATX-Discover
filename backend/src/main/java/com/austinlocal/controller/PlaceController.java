package com.austinlocal.controller;

import com.austinlocal.dto.RankedPlace;
import com.austinlocal.model.Place;
import com.austinlocal.service.PlaceService;
import com.austinlocal.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;
    private final RecommendationService recommendationService;

    @Autowired
    public PlaceController(PlaceService placeService, RecommendationService recommendationService) {
        this.placeService = placeService;
        this.recommendationService = recommendationService;
    }

    /**
     * GET /api/places/nearby?lat=30.267&lon=-97.743&radiusKm=5&category=coffee&userId=sam
     */
    @GetMapping("/nearby")
    public List<RankedPlace> nearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5.0") double radiusKm,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String userId
    ) {
        return placeService.findNearby(lat, lon, radiusKm, category, userId);
    }

    @GetMapping("/{id}")
    public Place getPlace(@PathVariable Long id) {
        return placeService.getById(id);
    }

    /**
     * POST /api/places/{id}/like  body: { "userId": "sam" }
     * Records a like, which updates the user's category affinity and
     * influences future search rankings.
     */
    @PostMapping("/{id}/like")
    public Map<String, String> like(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        Place place = placeService.getById(id);
        recommendationService.recordLike(userId, place.getCategory());
        return Map.of("status", "ok", "category", place.getCategory());
    }
}
