package Salesllmproject.salesllmproject.controller;

import Salesllmproject.salesllmproject.model.Lead;
import Salesllmproject.salesllmproject.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@CrossOrigin
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    public List<Lead> getAll() {
        return leadService.getAll();
    }

    @GetMapping("/{id}")
    public Lead getById(@PathVariable Long id) {
        return leadService.getById(id);
    }

    @PostMapping
    public Lead create(@RequestBody Lead lead) {
        return leadService.save(lead);
    }

    @PutMapping("/{id}")
    public Lead update(@PathVariable Long id, @RequestBody Lead lead) {
        lead.setId(id);
        return leadService.save(lead);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        leadService.delete(id);
    }
}