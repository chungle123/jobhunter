package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPagination;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO createJob(Job j) {
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }
        Job currentJob = this.jobRepository.save(j);

        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setLocation(currentJob.getLocation());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setEndDate(currentJob.getEndDate());
        dto.setStartDate(currentJob.getStartDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());
        if (currentJob.getSkills() != null) {
            List<String> jobSkills = j.getSkills().stream().map(x -> x.getName()).collect(Collectors.toList());
            dto.setSkills(jobSkills);
        }
        return dto;

    }

    public Optional<Job> featchById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResUpdateJobDTO update(Job job) {
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }
        Job currentJob = this.jobRepository.save(job);
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setLocation(currentJob.getLocation());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setEndDate(currentJob.getEndDate());
        dto.setStartDate(currentJob.getStartDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());
        if (currentJob.getSkills() != null) {
            List<String> jobSkills = job.getSkills().stream().map(x -> x.getName()).collect(Collectors.toList());
            dto.setSkills(jobSkills);
        }
        return dto;

    }

    public void deleteById(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPagination fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> page = this.jobRepository.findAll(spec, pageable);
        ResultPagination rs = new ResultPagination();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(page.getContent());
        return rs;
    }

}
