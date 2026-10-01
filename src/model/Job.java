package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Job Class - Represents a job opening posted by a company along with its eligibility criteria.
 */
public class Job {
    private String id;
    private String title;
    private String companyName;
    private String requiredBranch;
    private double minCgpa;
    private double minPercentage;
    private int maxArrears;
    private List<String> requiredSkills;

    // Default Constructor
    public Job() {
        this.requiredSkills = new ArrayList<>();
    }

    // Parameterized Constructor
    public Job(String id, String title, String companyName, String requiredBranch,
               double minCgpa, double minPercentage, int maxArrears, List<String> requiredSkills) {
        this.id = id;
        this.title = title;
        this.companyName = companyName;
        this.requiredBranch = requiredBranch;
        this.minCgpa = minCgpa;
        this.minPercentage = minPercentage;
        this.maxArrears = maxArrears;
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
    }

    // Helper Constructor for comma-separated skills
    public Job(String id, String title, String companyName, String requiredBranch,
               double minCgpa, double minPercentage, int maxArrears, String commaSeparatedSkills) {
        this(id, title, companyName, requiredBranch, minCgpa, minPercentage, maxArrears, parseSkills(commaSeparatedSkills));
    }

    private static List<String> parseSkills(String skillsStr) {
        List<String> list = new ArrayList<>();
        if (skillsStr != null && !skillsStr.trim().isEmpty()) {
            String[] parts = skillsStr.split(",");
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    list.add(part.trim());
                }
            }
        }
        return list;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRequiredBranch() {
        return requiredBranch;
    }

    public void setRequiredBranch(String requiredBranch) {
        this.requiredBranch = requiredBranch;
    }

    public double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public double getMinPercentage() {
        return minPercentage;
    }

    public void setMinPercentage(double minPercentage) {
        this.minPercentage = minPercentage;
    }

    public int getMaxArrears() {
        return maxArrears;
    }

    public void setMaxArrears(int maxArrears) {
        this.maxArrears = maxArrears;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
