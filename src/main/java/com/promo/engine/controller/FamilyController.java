package com.promo.engine.controller;

import com.promo.engine.dto.FamilyDTO;
import com.promo.engine.entity.Family;
import com.promo.engine.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping
    public ResponseEntity<Family> createFamily(@RequestBody FamilyDTO familyDTO) {
        return ResponseEntity.ok(familyService.createFamily(familyDTO));
    }

    @GetMapping
    public ResponseEntity<List<Family>> getAllFamilies() {
        return ResponseEntity.ok(familyService.getAllFamilies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Family> getFamilyById(@PathVariable Long id) {
        return ResponseEntity.ok(familyService.getFamilyById(id));
    }

    @GetMapping("/type/{familyType}")
    public ResponseEntity<List<Family>> getFamiliesByType(@PathVariable String familyType) {
        return ResponseEntity.ok(familyService.getFamiliesByType(familyType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Family> updateFamily(@PathVariable Long id, @RequestBody FamilyDTO familyDTO) {
        return ResponseEntity.ok(familyService.updateFamily(id, familyDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFamily(@PathVariable Long id) {
        familyService.deleteFamily(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{familyId}/members")
    public ResponseEntity<Family> addMembers(@PathVariable Long familyId, @RequestBody List<Long> memberIds) {
        return ResponseEntity.ok(familyService.addMembers(familyId, memberIds));
    }

    @DeleteMapping("/{familyId}/members")
    public ResponseEntity<Family> removeMembers(@PathVariable Long familyId, @RequestBody List<Long> memberIds) {
        return ResponseEntity.ok(familyService.removeMembers(familyId, memberIds));
    }

    @GetMapping("/{familyId}/members")
    public ResponseEntity<List<Long>> getFamilyMembers(@PathVariable Long familyId) {
        return ResponseEntity.ok(familyService.getFamilyMembers(familyId));
    }
} 