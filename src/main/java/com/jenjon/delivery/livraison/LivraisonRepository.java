package com.jenjon.delivery.livraison;

import com.jenjon.delivery.livraison.domain.BonDeLivraison;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivraisonRepository extends JpaRepository<BonDeLivraison, Long> {
    Optional<BonDeLivraison> findByNumeroBL(String numeroBL);
    List<BonDeLivraison> findByLivreurId(Long livreurId);
    Page<BonDeLivraison> findAll(Pageable pageable);
}
