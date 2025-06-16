package com.promo.engine.service.impl;

import com.promo.engine.dto.RewardDTO;
import com.promo.engine.domain.Reward;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.RewardRepository;
import com.promo.engine.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {
    private final RewardRepository rewardRepository;

    @Override
    @Transactional
    public Reward createReward(RewardDTO rewardDTO) {
        Reward reward = new Reward();
        reward.setRewardType(rewardDTO.getRewardType());
        reward.setValue(rewardDTO.getValue());
        reward.setTargetEntityType(rewardDTO.getTargetEntityType());
        reward.setTargetEntityId(rewardDTO.getTargetEntityId());
        reward.setDescription(rewardDTO.getDescription());
        return rewardRepository.save(reward);
    }

    @Override
    public Reward getRewardById(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found"));
    }

    @Override
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    @Override
    @Transactional
    public Reward updateReward(Long id, RewardDTO rewardDTO) {
        Reward reward = getRewardById(id);
        reward.setRewardType(rewardDTO.getRewardType());
        reward.setValue(rewardDTO.getValue());
        reward.setTargetEntityType(rewardDTO.getTargetEntityType());
        reward.setTargetEntityId(rewardDTO.getTargetEntityId());
        reward.setDescription(rewardDTO.getDescription());
        return rewardRepository.save(reward);
    }

    @Override
    @Transactional
    public void deleteReward(Long id) {
        Reward reward = getRewardById(id);
        rewardRepository.delete(reward);
    }
} 