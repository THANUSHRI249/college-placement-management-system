package model;

/**
 * Application Class - Represents a student's job application and recruitment status.
 * Status can be: Applied, Shortlisted, Selected, Rejected
 */
public class Application {
    private String id;
    private String studentRegNo;
    private String studentName;
    private String companyName;
    private String jobTitle;
    private String status; // Applied, Shortlisted, Selected, Rejected

    // Default Constructor
    public Application() {}

    // Parameterized Constructor
    public Application(String id, String studentRegNo, String studentName, 
                       String companyName, String jobTitle, String status) {
        this.id = id;
        this.studentRegNo = studentRegNo;
        this.studentName = studentName;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.status = status != null ? status : "Applied";
    }

    // Encapsulation - Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
