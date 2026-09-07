package com.jenjon.delivery.agence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgenceService {

    private final AgenceRepository agenceRepository;

    public Agence create(Agence agence) {
        return agenceRepository.save(agence);
    }

    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    public Agence findById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agence introuvable : " + id));
    }

    public Agence update(Long id, Agence updated) {
        Agence agence = findById(id);
        agence.setNom(updated.getNom());
        agence.setVille(updated.getVille());
        agence.setAdresse(updated.getAdresse());
        agence.setChefAgenceId(updated.getChefAgenceId());
        agence.setActive(updated.isActive());
        return agenceRepository.save(agence);
    }

    public void delete(Long id) {
        agenceRepository.deleteById(id);
    }
}
