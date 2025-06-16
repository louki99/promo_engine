package com.promo.engine.service;

import com.promo.engine.domain.Tier;
import com.promo.engine.dto.TierDTO;
import java.util.List;

public interface TierService {
    Tier createTier(TierDTO tierDTO);
    Tier getTierById(Long id);
    List<Tier> getAllTiers();
    Tier updateTier(Long id, TierDTO tierDTO);
    void deleteTier(Long id);
} 