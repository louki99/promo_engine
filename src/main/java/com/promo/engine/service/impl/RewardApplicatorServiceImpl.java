package com.promo.engine.service.impl;

import com.promo.engine.domain.PromoProduct;
import com.promo.engine.dto.*;
import com.promo.engine.domain.PromotionRule;
import com.promo.engine.domain.Tier;
import com.promo.engine.domain.Reward;
import com.promo.engine.enums.CalculationMethod;
import com.promo.engine.repository.PromoProductRepository;
import com.promo.engine.service.RewardApplicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardApplicatorServiceImpl implements RewardApplicatorService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final PromoProductRepository promoProductRepository;

    @Override
    public BigDecimal applyRuleRewards(PromotionRule rule, ApplyPromotionRequest request, ApplyPromotionResponse response) {
        if (rule.getTiers() == null || rule.getTiers().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Sort tiers by minimum threshold
        List<Tier> sortedTiers = rule.getTiers().stream()
                .sorted(Comparator.comparing(Tier::getMinimumThreshold))
                .collect(Collectors.toList());

        // Calculate breakpoint value based on rule type
        BigDecimal breakpointValue = calculateBreakpointValue(rule, request);

        // Find applicable tier
        Tier applicableTier = findApplicableTier(sortedTiers, breakpointValue);
        if (applicableTier == null) {
            return BigDecimal.ZERO;
        }

        // Apply reward based on calculation method
        if (rule.getCalculationMethod() == CalculationMethod.BRACKET) {
            return applyBracketReward(applicableTier, request, response);
        } else {
            return applyCumulativeReward(sortedTiers, breakpointValue, request, response);
        }
    }

    private BigDecimal calculateBreakpointValue(PromotionRule rule, ApplyPromotionRequest request) {
        return switch (rule.getBreakpointType()) {
            case AMOUNT -> calculateCartTotal(request.getCartItems());
            case QUANTITY -> calculateTotalQuantity(request.getCartItems());
            case SKU_POINTS -> calculateSkuPoints(request.getCartItems());
            case PROMOTION_TOTAL_USAGE -> BigDecimal.valueOf(request.getLoyaltyPoints());
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalQuantity(List<CartItem> cartItems) {
        return BigDecimal.valueOf(cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum());
    }

    private BigDecimal calculateSkuPoints(List<CartItem> cartItems) {
        // This would typically involve a more complex calculation based on SKU attributes
        return BigDecimal.valueOf(cartItems.size());
    }

    private Tier findApplicableTier(List<Tier> tiers, BigDecimal breakpointValue) {
        return tiers.stream()
                .filter(tier -> breakpointValue.compareTo(tier.getMinimumThreshold()) >= 0)
                .max(Comparator.comparing(Tier::getMinimumThreshold))
                .orElse(null);
    }

    private BigDecimal applyBracketReward(Tier tier, ApplyPromotionRequest request, ApplyPromotionResponse response) {
        Reward reward = tier.getReward();
        BigDecimal discount = BigDecimal.ZERO;

        switch (reward.getRewardType()) {
            case PERCENT_DISCOUNT_ON_ITEM:
                discount = applyPercentDiscountOnItem(reward, request, response);
                break;
            case PERCENT_DISCOUNT_ON_CART:
                discount = applyPercentDiscountOnCart(reward, request, response);
                break;
            case FIXED_DISCOUNT_ON_ITEM:
                discount = applyFixedDiscountOnItem(reward, request, response);
                break;
            case FIXED_DISCOUNT_ON_CART:
                discount = applyFixedDiscountOnCart(reward, request, response);
                break;
            case FREE_PRODUCT:
                applyFreeProduct(reward, request, response);
                break;
            case FREE_SHIPPING:
                applyFreeShipping(reward, response);
                break;
        }

        return discount;
    }

    private BigDecimal applyCumulativeReward(List<Tier> tiers, BigDecimal breakpointValue,
                                             ApplyPromotionRequest request, ApplyPromotionResponse response) {
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Tier tier : tiers) {
            if (breakpointValue.compareTo(tier.getMinimumThreshold()) >= 0) {
                totalDiscount = totalDiscount.add(applyBracketReward(tier, request, response));
            }
        }

        return totalDiscount;
    }

    private BigDecimal applyPercentDiscountOnItem(Reward reward, ApplyPromotionRequest request,
                                                  ApplyPromotionResponse response) {
        BigDecimal discount = BigDecimal.ZERO;

        for (CartItem cartItem : request.getCartItems()) {
            if (isTargetEntity(cartItem, reward)) {
                BigDecimal itemTotal = cartItem.getUnitPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                BigDecimal itemDiscount = itemTotal.multiply(reward.getValue())
                        .divide(HUNDRED, SCALE, ROUNDING_MODE);

                discount = discount.add(itemDiscount);
                updateLineItemDiscount(response, cartItem, itemDiscount);
            }
        }

        return discount;
    }


    private BigDecimal applyPercentDiscountOnCart(Reward reward, ApplyPromotionRequest request,
                                                  ApplyPromotionResponse response) {
        BigDecimal cartTotal = calculateCartTotal(request.getCartItems());
        BigDecimal discount = cartTotal.multiply(reward.getValue())
                .divide(HUNDRED, SCALE, ROUNDING_MODE);

        // Distribute discount proportionally across all items
        distributeDiscountProportionally(response, discount, request.getCartItems());

        return discount;
    }

    private BigDecimal applyFixedDiscountOnItem(Reward reward, ApplyPromotionRequest request,
                                                ApplyPromotionResponse response) {
        BigDecimal discount = BigDecimal.ZERO;

        for (CartItem cartItem : request.getCartItems()) {
            if (isTargetEntity(cartItem, reward)) {
                BigDecimal itemDiscount = reward.getValue()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                discount = discount.add(itemDiscount);
                updateLineItemDiscount(response, cartItem, itemDiscount);
            }
        }

        return discount;
    }

    private BigDecimal applyFixedDiscountOnCart(Reward reward, ApplyPromotionRequest request,
                                                ApplyPromotionResponse response) {
        BigDecimal discount = reward.getValue();
        distributeDiscountProportionally(response, discount, request.getCartItems());
        return discount;
    }

    private void applyFreeProduct(Reward reward, ApplyPromotionRequest request,
                                  ApplyPromotionResponse response) {
        FreeItem freeItem = new FreeItem();
        freeItem.setProductId(reward.getTargetEntityId());
        freeItem.setQuantity(1); // Default to 1 free item
        response.getFreeItems().add(freeItem);
    }

    private void applyFreeShipping(Reward reward, ApplyPromotionResponse response) {
        response.setShippingDiscount(response.getShippingDiscount() != null ?
                response.getShippingDiscount() : BigDecimal.ZERO);
    }

    private boolean isTargetEntity(CartItem item, Reward reward) {
        if (reward.getTargetEntityType().equals("PRODUCT")) {
            return item.getProductId().equals(reward.getTargetEntityId());
        } else if (reward.getTargetEntityType().equals("PRODUCT_FAMILY")) {
            PromoProduct product = promoProductRepository.findById(item.getProductId()).orElse(null);
            return product != null && product.getFamilyId() != null && product.getFamilyId().equals(reward.getTargetEntityId());
        }
        return false;
    }

    private void updateLineItemDiscount(ApplyPromotionResponse response, CartItem cartItem,
                                        BigDecimal discount) {
        response.getLineItems().stream()
                .filter(item -> item.getProductId().equals(cartItem.getProductId()))
                .findFirst()
                .ifPresent(item -> {
                    item.setTotalDiscount(item.getTotalDiscount().add(discount));
                    item.setFinalPrice(item.getOriginalPrice().subtract(item.getTotalDiscount()));
                });
    }

    private void distributeDiscountProportionally(ApplyPromotionResponse response,
                                                  BigDecimal totalDiscount, List<CartItem> cartItems) {
        BigDecimal cartTotal = calculateCartTotal(cartItems);

        for (CartItem cartItem : cartItems) {
            BigDecimal itemTotal = cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            BigDecimal proportion = itemTotal.divide(cartTotal, SCALE, ROUNDING_MODE);
            BigDecimal itemDiscount = totalDiscount.multiply(proportion);

            updateLineItemDiscount(response, cartItem, itemDiscount);
        }
    }
} 