package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.dto.ResponseDtoWrapper;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.service.JobService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    @ResponseBody
    public ResponseDto<RequestJobResponse> requestJob(@RequestBody @Valid RequestJobRequestDto dto) {
        Job job = jobService.createJob(UUID.randomUUID(), dto.start, dto.end);
        return ResponseDto.<RequestJobResponse>builder()
                .data(RequestJobResponse.builder()
                        .jobId(job.getId())
                        .build())
                .build();
    }


    @PatchMapping(path = "/{id}/cancel")
    @ResponseBody
    public ResponseDtoWrapper<Boolean> cancelJob(@PathVariable("id") UUID jobId) {
        ResponseDtoWrapper<Boolean> responseDtoWrapper = new ResponseDtoWrapper<>();
        List<Job> jobs = jobService.getJobs(jobId);
        if (jobs.size() == 0) {
            return responseDtoWrapper
                    .setData(false)
                    .addError(new ResponseDtoWrapper.Status(404, "Job not found" + jobId));

        }
        jobService.cancelJob(jobId);
        return responseDtoWrapper
                .setData(true);

    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseDtoWrapper<List<Job>> getJobs(@PathVariable("id") UUID jobId) {
        ResponseDtoWrapper<List<Job>> responseDtoWrapper = new ResponseDtoWrapper<>();

        return responseDtoWrapper.setData(jobService.getJobs(jobId));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private static class RequestJobRequestDto {
        @NotNull
        private UUID companyId;
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate start;
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate end;
    }

    @Builder
    @Data
    private static class RequestJobResponse {
        UUID jobId;
    }
}
