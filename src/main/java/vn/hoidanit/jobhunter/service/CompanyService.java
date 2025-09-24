package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public List<Company> fetchAll() {
        return this.companyRepository.findAll();
    }

    public Company fetchCompanyById(long id) {
        Optional<Company> c = this.companyRepository.findById(id);
        if (c.isPresent()) {
            return c.get();
        }
        return null;
    }

    public Company handleUpdateCompany(Company req) {
        Company c = fetchCompanyById(req.getId());
        if (c != null) {
            c.setName(req.getName());
            c.setDescription(req.getDescription());
            c.setAddress(req.getAddress());
            c.setLogo(req.getLogo());
            c = this.companyRepository.save(c);
        }
        return c;
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }

}
