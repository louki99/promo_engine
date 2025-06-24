package com.promo.engine.service;

import com.promo.engine.domain.PromotionEntity;
import com.promo.engine.dto.CreatePromotionRequest;
import com.promo.engine.dto.RuleDto;
import com.promo.engine.dto.ActionDto;
import com.promo.engine.repository.PromotionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PromotionManagementServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionManagementService promotionManagementService;

    @Test
    void createPromotion_ShouldCreatePromotionWithRulesAndActions() {
        // Given
        CreatePromotionRequest request = new CreatePromotionRequest();
        request.setName("Test Promotion");
        request.setDescription("Test Description");
        request.setStartDate(LocalDateTime.now());
        request.setEndDate(LocalDateTime.now().plusDays(7));
        request.setActive(true);
        request.setPriority(1);

        RuleDto rule = new RuleDto();
        rule.setType("BASKET_VALUE");
        rule.setParameters("{\"minimumValue\": 100.0}");
        request.setRules(List.of(rule));

        ActionDto action = new ActionDto();
        action.setType("PERCENTAGE_DISCOUNT");
        action.setParameters("{\"discountPercentage\": 10.0}");
        request.setActions(List.of(action));

        PromotionEntity savedPromotion = new PromotionEntity();
        savedPromotion.setId(1L);
        savedPromotion.setName(request.getName());
        when(promotionRepository.save(any(PromotionEntity.class))).thenReturn(savedPromotion);

        // When
        PromotionEntity result = promotionManagementService.createPromotion(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Promotion");
    }

    @Test
    void getPromotion_ShouldReturnPromotion() {
        // Given
        Long promotionId = 1L;
        PromotionEntity promotion = new PromotionEntity();
        promotion.setId(promotionId);
        promotion.setName("Test Promotion");
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));

        // When
        PromotionEntity result = promotionManagementService.getPromotion(promotionId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(promotionId);
        assertThat(result.getName()).isEqualTo("Test Promotion");
    }
} 