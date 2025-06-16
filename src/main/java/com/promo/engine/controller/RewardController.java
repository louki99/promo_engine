package com.promo.engine.controller;

import com.promo.engine.dto.RewardDTO;
import com.promo.engine.domain.Reward;
import com.promo.engine.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
public class RewardController {
    
    private final RewardService rewardService;
    
    @PostMapping
    public ResponseEntity<Reward> createReward(@RequestBody RewardDTO rewardDTO) {
        return ResponseEntity.ok(rewardService.createReward(rewardDTO));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Reward> getRewardById(@PathVariable Long id) {
        return ResponseEntity.ok(rewardService.getRewardById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Reward> updateReward(
            @PathVariable Long id,
            @RequestBody RewardDTO rewardDTO) {
        return ResponseEntity.ok(rewardService.updateReward(id, rewardDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        rewardService.deleteReward(id);
        return ResponseEntity.ok().build();
    }
}