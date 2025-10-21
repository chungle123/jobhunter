package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.dto.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPagination;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.errors.IdInvalidException;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job j) {
        return ResponseEntity.status(HttpStatus.CREATED.value())
                .body(this.jobService.createJob(j));
    }

    @PutMapping("/jobs")
    @ApiMessage("update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.featchById(job.getId());
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job not found!");
        }
        return ResponseEntity.ok().body(this.jobService.update(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete a job")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.featchById(id);
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job not found!");
        }
        this.jobService.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("get job by id")
    public ResponseEntity<Job> fetchJob(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.featchById(id);
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job not found!");
        }
        return ResponseEntity.ok().body(jobOptional.get());
    }

    @GetMapping("/jobs")
    @ApiMessage("get all job ")
    public ResponseEntity<ResultPagination> fetchAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.fetchAll(spec, pageable));
    }
}
