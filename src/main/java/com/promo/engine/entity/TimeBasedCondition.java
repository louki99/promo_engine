package com.promo.engine.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "time_based_conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeBasedCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalTime startTime;
    
    @Column(nullable = false)
    private LocalTime endTime;
    
    @ElementCollection
    @CollectionTable(name = "time_condition_applicable_days")
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> applicableDays;
    
    @Column(nullable = false)
    private String timeZone;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @ElementCollection
    @CollectionTable(name = "time_condition_seasonal_factors")
    @MapKeyColumn(name = "month")
    @Column(name = "multiplier")
    private Map<Integer, Double> seasonalMultipliers;
    
    @Column(name = "is_recurring")
    private Boolean isRecurring = false;
    
    @Column(name = "recurrence_interval")
    private Integer recurrenceInterval;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_unit")
    private ChronoUnit recurrenceUnit;
    
    @ManyToOne
    @JoinColumn(name = "condition_id")
    private Condition condition;
    
    public boolean isApplicable() {
        ZoneId zone = ZoneId.of(timeZone);
        LocalTime now = LocalTime.now(zone);
        LocalDate today = LocalDate.now(zone);
        DayOfWeek currentDay = today.getDayOfWeek();
        
        // Check if within date range
        if (startDate != null && today.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && today.isAfter(endDate)) {
            return false;
        }
        
        // Check if within time range
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            return false;
        }
        
        // Check if day is applicable
        if (!applicableDays.contains(currentDay)) {
            return false;
        }
        
        // Check recurrence if applicable
        if (isRecurring && recurrenceInterval != null && recurrenceUnit != null) {
            return checkRecurrence(today);
        }
        
        return true;
    }
    
    private boolean checkRecurrence(LocalDate today) {
        if (startDate == null) {
            return false;
        }
        
        long daysBetween = ChronoUnit.DAYS.between(startDate, today);
        long unitsBetween = recurrenceUnit.between(startDate, today);
        
        return unitsBetween % recurrenceInterval == 0;
    }
    
    public double getSeasonalMultiplier() {
        if (seasonalMultipliers == null || seasonalMultipliers.isEmpty()) {
            return 1.0;
        }
        
        int currentMonth = LocalDate.now(ZoneId.of(timeZone)).getMonthValue();
        return seasonalMultipliers.getOrDefault(currentMonth, 1.0);
    }
    
    public boolean isWithinTimeRange(LocalTime time) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }
    
    public boolean isWithinDateRange(LocalDate date) {
        return (startDate == null || !date.isBefore(startDate)) &&
               (endDate == null || !date.isAfter(endDate));
    }
} 