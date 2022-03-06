package com.zenjob.challenge.service;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.repository.JobRepository;
import com.zenjob.challenge.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@Repository
@Transactional
public class JobService {
    private final JobRepository jobRepository;
    private final ShiftRepository shiftRepository;

    public Job createJob(UUID uuid, LocalDate date1, LocalDate date2) {
        Job job = Job.builder()
                .id(uuid)
                .companyId(UUID.randomUUID())
                .startTime(date1.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                .endTime(date2.atTime(17, 0, 0).toInstant(ZoneOffset.UTC))
                .build();
        job.setShifts(LongStream.range(0, ChronoUnit.DAYS.between(date1, date2))
                .mapToObj(idx -> date1.plus(idx, ChronoUnit.DAYS))
                .map(date -> Shift.builder()
                        .id(UUID.randomUUID())
                        .job(job)
                        .startTime(date.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                        .endTime(date.atTime(17, 0, 0).toInstant(ZoneOffset.UTC))
                        .build())
                .collect(Collectors.toList()));
        return jobRepository.save(job);
    }

    /*
    Task A
        AS a company
        I CAN cancel a job I ordered previously
        AND if the job gets cancelled all of its shifts get cancelled as well
     */

    public Boolean cancelJob(UUID id) {

        Long siftCount = shiftRepository.findAllByJob_Id(id).stream()
                .map(shift -> shiftRepository.saveAndFlush(shift.setIsCanceled(true))).count();
        Long jobCount = jobRepository.findAllById(id)
                .stream().map(
                        job -> jobRepository.saveAndFlush(job.setIsCanceled(true))
                ).count();
        return siftCount > 0 || jobCount > 0;
    }

    /*
      Task B
            AS a company
            I CAN cancel a single shift of a job I ordered previously
     */
    public Boolean cancelSiftBySiftId(UUID siftId) {

        Long siftCount = shiftRepository.findAllById(siftId).stream()
                .map(shift -> shiftRepository.saveAndFlush(shift.setIsCanceled(true))).count();

        return siftCount == 1;
    }


    /*
    Task C
        AS a company
        I CAN cancel all of my shifts which were booked for a specific talent
        AND replacement shifts are created with the same dates
     */

    public Boolean cancelSiftByTanentId(UUID tanentId) {

        Long siftCount = shiftRepository.findAllByTalentId(tanentId).stream()
                .map(shift -> shiftRepository.saveAndFlush(shift.setIsCanceled(true))).count();

        // AND replacement shifts are created with the same dates ??
        // Presumption for any tanent as for particular tanent all tasks are already canceled

        return siftCount == 1;
    }

    public List<Shift> getShifts(UUID id) {
        return shiftRepository.findAllByJob_Id(id);
    }

    public List<Job> getJobs(UUID id) {
        return jobRepository.findAllById(id);
    }

    public void bookTalent(UUID talent, UUID shiftId) {
        shiftRepository.findById(shiftId).map(shift -> shiftRepository.save(shift.setTalentId(talent)));
    }
}
