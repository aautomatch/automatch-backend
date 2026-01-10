package com.automatch.portal.service.unauthenticated;

import com.automatch.portal.dao.unauthenticated.InstructorPublicDAO;
import com.automatch.portal.records.InstructorPublicRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstructorPublicService {

    private final InstructorPublicDAO instructorPublicDAO;

    // Busca todos os instrutores
    public List<InstructorPublicRecord> getAllInstructors() {
        return instructorPublicDAO.findAll();
    }

    // Busca instrutores verificados
    public List<InstructorPublicRecord> getVerifiedInstructors() {
        return instructorPublicDAO.findVerified();
    }

    // Busca com filtros (agora incluindo cidade)
    public List<InstructorPublicRecord> searchInstructors(
            String term,
            Integer minYearsExperience,
            BigDecimal maxHourlyRate,
            BigDecimal minRating
    ) {

        if (minYearsExperience != null && minYearsExperience < 0) {
            throw new IllegalArgumentException("Minimum years of experience cannot be negative");
        }

        if (maxHourlyRate != null && maxHourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Maximum hourly rate must be greater than zero");
        }

        if (minRating != null &&
                (minRating.compareTo(BigDecimal.ZERO) < 0 ||
                        minRating.compareTo(BigDecimal.valueOf(5)) > 0)) {
            throw new IllegalArgumentException("Minimum rating must be between 0 and 5");
        }

        return instructorPublicDAO.search(
                term,
                minYearsExperience,
                maxHourlyRate,
                minRating
        );
    }


    // Busca os melhores avaliados
    public List<InstructorPublicRecord> getTopRatedInstructors(int limit) {
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100");
        }
        return instructorPublicDAO.findTopRated(limit);
    }

    // Busca instrutores disponíveis agora
    public List<InstructorPublicRecord> getAvailableInstructorsNow() {
        return instructorPublicDAO.findAvailableNow();
    }

    // Busca por faixa de preço (com cidade opcional)
    public List<InstructorPublicRecord> getInstructorsByHourlyRateRange(BigDecimal minRate, BigDecimal maxRate, String city) {
        if (minRate != null && maxRate != null && minRate.compareTo(maxRate) > 0) {
            throw new IllegalArgumentException("Minimum rate cannot be greater than maximum rate");
        }
        return instructorPublicDAO.findByHourlyRateRange(minRate, maxRate, city);
    }

    // Busca por faixa de experiência (com cidade opcional)
    public List<InstructorPublicRecord> getInstructorsByExperienceRange(Integer minYears, Integer maxYears, String city) {
        if (minYears != null && maxYears != null && minYears > maxYears) {
            throw new IllegalArgumentException("Minimum years cannot be greater than maximum years");
        }
        return instructorPublicDAO.findByExperienceRange(minYears, maxYears, city);
    }

    // Buscar instrutores por cidade
    public List<InstructorPublicRecord> getInstructorsByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        return instructorPublicDAO.findByCity(city);
    }

    // Buscar todas as cidades disponíveis
    public List<String> getAllCities() {
        return instructorPublicDAO.findAllCities();
    }

    // Métodos de contagem e estatísticas
    public int countInstructors() {
        return instructorPublicDAO.countAll();
    }

    public int countVerifiedInstructors() {
        return instructorPublicDAO.countVerified();
    }

    public Object getHourlyRateStats() {
        return instructorPublicDAO.getHourlyRateStats();
    }
}