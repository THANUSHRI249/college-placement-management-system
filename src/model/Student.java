package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Student Class - Represents a student profile in the College Placement System.
 * Demonstrates Encapsulation (private attributes with getters/setters)
 * and Constructor usage.
 */
public class Student {
    private String name;
    private String registerNumber;
    private String department;
    private double cgpa;
    private double percentage;
    private int arrears;
    private List<String> skills;
    private String contactInfo;

    // Default Constructor
    public Student() {
        this.skills = new ArrayList<>();
    }

    // Parameterized Constructor
    public Student(String name, String registerNumber, String department, double cgpa, 
                   double percentage, int arrears, List<String> skills, String contactInfo) {
        this.name = name;
        this.registerNumber = registerNumber;
        this.department = department;
        this.cgpa = cgpa;
        this.percentage = percentage;
        this.arrears = arrears;
        this.skills = skills != null ? skills : new ArrayList<>();
        this.contactInfo = contactInfo;
    }

    // Helper Constructor with Skills as comma-separated String
    public Student(String name, String registerNumber, String department, double cgpa, 
                   double percentage, int arrears, String commaSeparatedSkills, String contactInfo) {
        this(name, registerNumber, department, cgpa, percentage, arrears, parseSkills(commaSeparatedSkills), contactInfo);
    }

    private static List<String> parseSkills(String commaSeparatedSkills) {
        List<String> list = new ArrayList<>();
        if (commaSeparatedSkills != null && !commaSeparatedSkills.trim().isEmpty()) {
            String[] parts = commaSeparatedSkills.split(",");
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    list.add(part.trim());
                }
            }
        }
        return list;
    }

    // Encapsulation - Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getArrears() {
        return arrears;
    }

    public void setArrears(int arrears) {
        this.arrears = arrears;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
