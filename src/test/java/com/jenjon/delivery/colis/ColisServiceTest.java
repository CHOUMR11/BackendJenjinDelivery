package com.jenjon.delivery.colis;

import com.jenjon.delivery.colis.domain.Colis;
import com.jenjon.delivery.colis.domain.StatutColis;
import com.jenjon.delivery.colis.domain.TypeColis;
import com.jenjon.delivery.suivi.EvenementSuiviService;
import com.jenjon.delivery.suivi.domain.EvenementSuivi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColisServiceTest {

    @Mock
    private ColisRepository colisRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private EvenementSuiviService evenementSuiviService;

    @InjectMocks
    private ColisService colisService;

    private Colis colisSansBarcode;

    @BeforeEach
    void setUp() {
        colisSansBarcode = Colis.builder()
                .type(TypeColis.COLIS)
                .expediteurNom("Boutique Test")
                .expediteurTelephone("20000000")
                .designation("Vêtements")
                .quantite(1)
                .build();
    }

    @Test
    void create_genereUnCodeBarres_siAbsent() {
        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> inv.getArgument(0));

        Colis result = colisService.create(colisSansBarcode);

        assertThat(result.getCodeBarres()).isNotBlank();
        assertThat(result.getStatut()).isEqualTo(StatutColis.ENREGISTRE);
        verify(colisRepository).save(any(Colis.class));
    }

    @Test
    void create_conserveLeCodeBarres_siDejaFourni() {
        colisSansBarcode.setCodeBarres("JD-EXISTANT-001");
        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> inv.getArgument(0));

        Colis result = colisService.create(colisSansBarcode);

        assertThat(result.getCodeBarres()).isEqualTo("JD-EXISTANT-001");
    }

    @Test
    void updateStatut_publieUnEvenement() {
        Colis colisExistant = Colis.builder().id(1L).codeBarres("JD-001").statut(StatutColis.ENREGISTRE).build();
        when(colisRepository.findById(1L)).thenReturn(Optional.of(colisExistant));
        when(colisRepository.save(any(Colis.class))).thenAnswer(inv -> inv.getArgument(0));

        Colis result = colisService.updateStatut(1L, StatutColis.LIVRE, "20000000", null, "SMS");

        assertThat(result.getStatut()).isEqualTo(StatutColis.LIVRE);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void findById_lanceUneExceptionSiIntrouvable() {
        when(colisRepository.findById(99L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> colisService.findById(99L));
    }
}
