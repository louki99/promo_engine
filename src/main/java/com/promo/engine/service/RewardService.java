package com.promo.engine.service;

import com.promo.engine.entity.Reward;
import com.promo.engine.dto.RewardDTO;
import java.util.List;

public interface RewardService {
    Reward createReward(RewardDTO rewardDTO);
    Reward getRewardById(Long id);
    List<Reward> getAllRewards();
    Reward updateReward(Long id, RewardDTO rewardDTO);
    void deleteReward(Long id);
} 