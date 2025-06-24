package com.promo.engine.service;

import com.promo.engine.domain.PromotionEntity;
import com.promo.engine.dto.*;
import com.promo.engine.repository.PromotionCustomerUsageRepository;
import com.promo.engine.repository.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PromotionEngineServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionCustomerUsageRepository usageRepository;

    @Mock
    private ConditionEvaluatorService conditionEvaluator;

    @Mock
    private RewardApplicatorService rewardApplicator;

    @InjectMocks
    private PromotionEngineService promotionEngineService;

    private ApplyPromotionRequest request;
    private PromotionEntity exclusivePromotion;
    private PromotionEntity nonExclusivePromotion1;
    private PromotionEntity nonExclusivePromotion2;

    @BeforeEach
    void setUp() {
        // Setup test request
        request = new ApplyPromotionRequest();
        request.setCustomerId(1L);
        CartItem item = new CartItem();
        item.setProductId(1L);
        item.setProductName("Test Product");
        item.setUnitPrice(BigDecimal.valueOf(100));
        item.setQuantity(1);
        request.setCartItems(List.of(item));

        // Setup test promotions
        exclusivePromotion = new PromotionEntity();
        exclusivePromotion.setId(1L);
        exclusivePromotion.setName("Exclusive 20% Off");
        exclusivePromotion.setPromoCode("EX20");
        exclusivePromotion.setPriority(10);
        exclusivePromotion.setExclusive(true);
        exclusivePromotion.setActive(true);

        nonExclusivePromotion1 = new PromotionEntity();
        nonExclusivePromotion1.setId(2L);
        nonExclusivePromotion1.setName("10% Off");
        nonExclusivePromotion1.setPromoCode("REG10");
        nonExclusivePromotion1.setPriority(5);
        nonExclusivePromotion1.setExclusive(false);
        nonExclusivePromotion1.setActive(true);

        nonExclusivePromotion2 = new PromotionEntity();
        nonExclusivePromotion2.setId(3L);
        nonExclusivePromotion2.setName("5% Off");
        nonExclusivePromotion2.setPromoCode("REG5");
        nonExclusivePromotion2.setPriority(1);
        nonExclusivePromotion2.setExclusive(false);
        nonExclusivePromotion2.setActive(true);
    }

    @Test
    void whenExclusivePromotionApplies_shouldNotApplyOthers() {
        // Given
        when(promotionRepository.findActivePromotions(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(exclusivePromotion, nonExclusivePromotion1, nonExclusivePromotion2));
        when(conditionEvaluator.evaluateRule(any(), any())).thenReturn(true);
        when(rewardApplicator.applyRuleRewards(any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(20)); // 20% off 100

        // When
        ApplyPromotionResponse response = promotionEngineService.calculatePromotions(request);

        // Then
        assertThat(response.getAppliedPromotions()).hasSize(1);
        assertThat(response.getAppliedPromotions().get(0).getPromoCode()).isEqualTo("EX20");
        assertThat(response.getDiscountTotal()).isEqualByComparingTo(BigDecimal.valueOf(20));
        verify(usageRepository, times(1)).save(any());
    }

    @Test
    void whenNoExclusivePromotion_shouldApplyStackablePromotions() {
        // Given
        when(promotionRepository.findActivePromotions(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(nonExclusivePromotion1, nonExclusivePromotion2));
        when(conditionEvaluator.evaluateRule(any(), any())).thenReturn(true);
        when(rewardApplicator.applyRuleRewards(any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(10)) // First promotion: 10% off 100
            .thenReturn(BigDecimal.valueOf(5));  // Second promotion: 5% off remaining

        // When
        ApplyPromotionResponse response = promotionEngineService.calculatePromotions(request);

        // Then
        assertThat(response.getAppliedPromotions()).hasSize(2);
        assertThat(response.getDiscountTotal()).isEqualByComparingTo(BigDecimal.valueOf(15));
        verify(usageRepository, times(2)).save(any());
    }

    @Test
    void whenPromotionNotApplicable_shouldSkip() {
        // Given
        when(promotionRepository.findActivePromotions(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(nonExclusivePromotion1, nonExclusivePromotion2));
        when(conditionEvaluator.evaluateRule(any(), any())).thenReturn(false);

        // When
        ApplyPromotionResponse response = promotionEngineService.calculatePromotions(request);

        // Then
        assertThat(response.getAppliedPromotions()).isEmpty();
        assertThat(response.getDiscountTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(usageRepository, never()).save(any());
    }

    @Test
    void whenStackingLimitReached_shouldNotApplyMorePromotions() {
        // Given
        nonExclusivePromotion1.setMaxStackCount(1);
        nonExclusivePromotion1.setStackingGroup("GROUP1");
        nonExclusivePromotion2.setStackingGroup("GROUP1");
        
        when(promotionRepository.findActivePromotions(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(nonExclusivePromotion1, nonExclusivePromotion2));
        when(conditionEvaluator.evaluateRule(any(), any())).thenReturn(true);
        when(rewardApplicator.applyRuleRewards(any(), any(), any()))
            .thenReturn(BigDecimal.valueOf(10)); // 10% off 100

        // When
        ApplyPromotionResponse response = promotionEngineService.calculatePromotions(request);

        // Then
        assertThat(response.getAppliedPromotions()).hasSize(1);
        assertThat(response.getDiscountTotal()).isEqualByComparingTo(BigDecimal.valueOf(10));
        verify(usageRepository, times(1)).save(any());
    }
} 