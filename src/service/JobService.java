package service;

import model.Company;
import model.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * JobService Class - Manages Companies and Job opportunities.
 * Demonstrates Collections (ArrayList) and methods for CRUD operations.
 */
public class JobService {
    private List<Company> companies;
    private List<Job> jobs;

    public JobService() {
        this.companies = new ArrayList<>();
        this.jobs = new ArrayList<>();
    }

    // Company Operations
    public void addCompany(Company company) {
        if (company != null) {
            companies.add(company);
        }
    }

    public List<Company> getAllCompanies() {
        return companies;
    }

    public Company getCompanyById(String id) {
        for (Company c : companies) {
            if (c.getId() != null && c.getId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    // Job Operations
    public void addJob(Job job) {
        if (job != null) {
            jobs.add(job);
        }
    }

    public List<Job> getAllJobs() {
        return jobs;
    }

    public Job getJobById(String id) {
        for (Job j : jobs) {
            if (j.getId() != null && j.getId().equalsIgnoreCase(id)) {
                return j;
            }
        }
        return null;
    }
}
