package com.insurancemegacorp.dbserver.service;

import com.insurancemegacorp.dbserver.dto.JobStatusDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;

@Service
public class JobTrackingService {
    
    private final ConcurrentMap<String, JobStatusDto> jobs = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CompletableFuture<?>> jobFutures = new ConcurrentHashMap<>();

    public String createJob(String description) {
        String jobId = UUID.randomUUID().toString();
        JobStatusDto jobStatus = new JobStatusDto(jobId, "PENDING");
        jobStatus.setMessage(description);
        jobStatus.setStartTime(System.currentTimeMillis());
        jobStatus.setProgress(0);
        
        jobs.put(jobId, jobStatus);
        return jobId;
    }

    public void startJob(String jobId) {
        JobStatusDto job = jobs.get(jobId);
        if (job != null) {
            job.setStatus("RUNNING");
            job.setStartTime(System.currentTimeMillis());
        }
    }

    public void updateJobProgress(String jobId, int progress, String message) {
        JobStatusDto job = jobs.get(jobId);
        if (job != null) {
            job.setProgress(progress);
            if (message != null) {
                job.setMessage(message);
            }
        }
    }

    public void completeJob(String jobId, Object result) {
        JobStatusDto job = jobs.get(jobId);
        if (job != null) {
            job.setStatus("COMPLETED");
            job.setProgress(100);
            job.setEndTime(System.currentTimeMillis());
            job.setResult(result);
        }
        
        // Clean up the future
        jobFutures.remove(jobId);
    }

    public void failJob(String jobId, String errorMessage) {
        JobStatusDto job = jobs.get(jobId);
        if (job != null) {
            job.setStatus("FAILED");
            job.setEndTime(System.currentTimeMillis());
            job.setMessage(errorMessage);
        }
        
        // Clean up the future
        jobFutures.remove(jobId);
    }

    public JobStatusDto getJobStatus(String jobId) {
        return jobs.get(jobId);
    }

    public void registerJobFuture(String jobId, CompletableFuture<?> future) {
        jobFutures.put(jobId, future);
        
        // Handle completion/failure
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                failJob(jobId, throwable.getMessage());
            }
        });
    }

    public boolean cancelJob(String jobId) {
        CompletableFuture<?> future = jobFutures.get(jobId);
        if (future != null && !future.isDone()) {
            future.cancel(true);
            
            JobStatusDto job = jobs.get(jobId);
            if (job != null) {
                job.setStatus("CANCELLED");
                job.setEndTime(System.currentTimeMillis());
            }
            
            jobFutures.remove(jobId);
            return true;
        }
        return false;
    }

    public void cleanupOldJobs() {
        long cutoff = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours
        
        jobs.entrySet().removeIf(entry -> {
            JobStatusDto job = entry.getValue();
            Long endTime = job.getEndTime();
            return endTime != null && endTime < cutoff;
        });
    }
}