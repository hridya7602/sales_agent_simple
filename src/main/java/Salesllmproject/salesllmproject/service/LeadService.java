package Salesllmproject.salesllmproject.service;

import Salesllmproject.salesllmproject.model.Lead;
import Salesllmproject.salesllmproject.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepo;

    public List<Lead> getAll() {
        return leadRepo.findAll();
    }

    public Lead getById(Long id) {
        return leadRepo.findById(id).orElseThrow();
    }

    public Lead save(Lead lead) {
        return leadRepo.save(lead);
    }

    public void delete(Long id) {
        leadRepo.deleteById(id);
    }
}
