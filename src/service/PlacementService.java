package service;

import interfaces.EligibilityChecker;
import model.Application;
import model.Job;
import model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PlacementService Class - Central service for managing student records,
 * job applications, placement tracking, and simple report generation.
 * Demonstrates Collections (ArrayList), Loops, and Methods.
 */
public class PlacementService {
    private List<Student> students;
    private List<Application> applications;
    private int nextAppId = 101;

    public PlacementService() {
        this.students = new ArrayList<>();
        this.applications = new ArrayList<>();
    }

    // Student Management
    public void addStudent(Student student) {
        if (student != null) {
            // Prevent duplicate register numbers by updating if exists
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getRegisterNumber().equalsIgnoreCase(student.getRegisterNumber())) {
                    students.set(i, student);
                    return;
                }
            }
            students.add(student);
        }
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public Student getStudentByRegNo(String regNo) {
        for (Student s : students) {
            if (s.getRegisterNumber().equalsIgnoreCase(regNo)) {
                return s;
            }
        }
        return null;
    }

    // Application Tracking
    public Application applyForJob(Student student, Job job) {
        if (student == null || job == null) {
            return null;
        }

        // Check if application already exists
        for (Application app : applications) {
            if (app.getStudentRegNo().equalsIgnoreCase(student.getRegisterNumber()) &&
                app.getJobTitle().equalsIgnoreCase(job.getTitle()) &&
                app.getCompanyName().equalsIgnoreCase(job.getCompanyName())) {
                return app; // return existing application
            }
        }

        String appId = "APP" + (nextAppId++);
        Application app = new Application(appId, student.getRegisterNumber(), student.getName(),
                job.getCompanyName(), job.getTitle(), "Applied");
        applications.add(app);
        return app;
    }

    public boolean updateApplicationStatus(String applicationId, String newStatus) {
        for (Application app : applications) {
            if (app.getId().equalsIgnoreCase(applicationId)) {
                app.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    public List<Application> getAllApplications() {
        return applications;
    }

    // Eligibility matching helper for student profile view
    public List<JobEligibilityMapping> getEligibleJobsForStudent(Student student, List<Job> jobs, EligibilityChecker checker) {
        List<JobEligibilityMapping> list = new ArrayList<>();
        if (student == null || jobs == null) return list;

        for (Job job : jobs) {
            EligibilityResult res = checker.evaluateEligibility(student, job);
            list.add(new JobEligibilityMapping(job, res));
        }
        return list;
    }

    // Simple Report Generation
    public Map<String, Object> generateReport() {
        Map<String, Object> report = new HashMap<>();
        int totalApplied = applications.size();
        int totalShortlisted = 0;
        int totalSelected = 0;
        int totalRejected = 0;

        for (Application app : applications) {
            String status = app.getStatus();
            if ("Shortlisted".equalsIgnoreCase(status)) {
                totalShortlisted++;
            } else if ("Selected".equalsIgnoreCase(status)) {
                totalSelected++;
            } else if ("Rejected".equalsIgnoreCase(status)) {
                totalRejected++;
            }
        }

        report.put("totalStudents", students.size());
        report.put("totalApplications", totalApplied);
        report.put("totalShortlisted", totalShortlisted);
        report.put("totalSelected", totalSelected);
        report.put("totalRejected", totalRejected);
        report.put("applications", applications);

        return report;
    }

    // Helper inner class for Job Eligibility Mapping
    public static class JobEligibilityMapping {
        private Job job;
        private EligibilityResult result;

        public JobEligibilityMapping(Job job, EligibilityResult result) {
            this.job = job;
            this.result = result;
        }

        public Job getJob() {
            return job;
        }

        public EligibilityResult getResult() {
            return result;
        }
    }
}
