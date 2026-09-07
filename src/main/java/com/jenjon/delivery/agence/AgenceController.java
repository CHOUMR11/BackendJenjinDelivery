package com.jenjon.delivery.agence;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agences")
@RequiredArgsConstructor
public class AgenceController {

    private final AgenceService agenceService;

    @PostMapping
    public ResponseEntity<Agence> create(@RequestBody Agence agence) {
        return ResponseEntity.ok(agenceService.create(agence));
    }

    @GetMapping
    public ResponseEntity<List<Agence>> findAll() {
        return ResponseEntity.ok(agenceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agence> findById(@PathVariable Long id) {
        return ResponseEntity.ok(agenceService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agence> update(@PathVariable Long id, @RequestBody Agence agence) {
        return ResponseEntity.ok(agenceService.update(id, agence));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
