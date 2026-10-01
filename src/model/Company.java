package model;

/**
 * Company Class - Stores details about visiting companies.
 * Demonstrates Encapsulation and Object creation.
 */
public class Company {
    private String id;
    private String name;
    private String industry;
    private String jobRole;

    // Default Constructor
    public Company() {}

    // Parameterized Constructor
    public Company(String id, String name, String industry, String jobRole) {
        this.id = id;
        this.name = name;
        this.industry = industry;
        this.jobRole = jobRole;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }
}
