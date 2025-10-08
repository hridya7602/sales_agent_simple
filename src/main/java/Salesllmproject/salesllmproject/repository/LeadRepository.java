package Salesllmproject.salesllmproject.repository;

import Salesllmproject.salesllmproject.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    // ✅ ADD THIS - Required by AnalyzeDealTool
    Optional<Lead> findByCompanyName(String companyName);

    // ✅ Optional: Case-insensitive search (more robust)
    @Query("SELECT l FROM Lead l WHERE LOWER(l.companyName) = LOWER(:companyName)")
    Optional<Lead> findByCompanyNameIgnoreCase(@Param("companyName") String companyName);

    // Your existing method
    @Query("SELECT l FROM Lead l WHERE FUNCTION('DATEDIFF', CURRENT_DATE, l.lastContactDate) > :days AND l.status = 'UNQUALIFIED'")
    List<Lead> findStaleLeads(@Param("days") int days);

    // ✅ BONUS: Useful additional queries for agents

    // Find all unqualified leads
    List<Lead> findByStatus(String status);

    // Find leads by industry
    List<Lead> findByIndustry(String industry);

    // Find large companies (for prioritization)
    @Query("SELECT l FROM Lead l WHERE l.companySize >= :minSize ORDER BY l.companySize DESC")
    List<Lead> findLargeCompanies(@Param("minSize") int minSize);

    // Find hot leads (qualified + recent contact)
    @Query("SELECT l FROM Lead l WHERE l.status = 'QUALIFIED' AND l.lastContactDate IS NOT NULL ORDER BY l.lastContactDate DESC")
    List<Lead> findHotLeads();

    // Search leads by keyword in notes
    @Query("SELECT l FROM Lead l WHERE LOWER(l.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Lead> searchByNotes(@Param("keyword") String keyword);
}