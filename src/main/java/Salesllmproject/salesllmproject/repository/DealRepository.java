package Salesllmproject.salesllmproject.repository;

import Salesllmproject.salesllmproject.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealRepository extends JpaRepository<Deal, Long> {
    Deal findByCompanyName(String companyName);
}


