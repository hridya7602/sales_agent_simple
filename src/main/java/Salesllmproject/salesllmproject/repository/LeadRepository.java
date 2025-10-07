package Salesllmproject.salesllmproject.repository;


import Salesllmproject.salesllmproject.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    @Query("SELECT l FROM Lead l WHERE FUNCTION('DATEDIFF', CURRENT_DATE, l.lastContactDate) > :days AND l.status = 'UNQUALIFIED'")
    List<Lead> findStaleLeads(@Param("days") int days);
}