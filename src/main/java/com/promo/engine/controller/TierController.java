package com.promo.engine.controller;

import com.promo.engine.dto.TierDTO;
import com.promo.engine.entity.Tier;
import com.promo.engine.service.TierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tiers")
@RequiredArgsConstructor
public class TierController {
    
    private final TierService tierService;
    
    @PostMapping
    public ResponseEntity<Tier> createTier(@RequestBody TierDTO tierDTO) {
        return ResponseEntity.ok(tierService.createTier(tierDTO));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Tier> getTierById(@PathVariable Long id) {
        return ResponseEntity.ok(tierService.getTierById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Tier>> getAllTiers() {
        return ResponseEntity.ok(tierService.getAllTiers());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Tier> updateTier(
            @PathVariable Long id,
            @RequestBody TierDTO tierDTO) {
        return ResponseEntity.ok(tierService.updateTier(id, tierDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTier(@PathVariable Long id) {
        tierService.deleteTier(id);
        return ResponseEntity.ok().build();
    }
} 