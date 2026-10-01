# College Placement Management System

**Done by THANUSHRI K, VALARMATHI R, SHALINI D**

---

## 📌 Project Overview
The **College Placement Management System** is a functional academic Java project built using core **Object-Oriented Programming (OOP)** principles and a modern, responsive HTML/CSS frontend. It automates student profile management, company job posting, student-job eligibility verification, application tracking, and basic placement analytics.

---

## 🏗 Project Architecture & OOP Structure

The codebase is organized into standard Java packages:

```text
college-placement-system/
├── src/
│   ├── model/
│   │   ├── Student.java           # Encapsulated Student model (CGPA, Branch, Arrears, Skills, etc.)
│   │   ├── Company.java           # Company details model
│   │   ├── Job.java               # Job opening model with eligibility criteria
│   │   └── Application.java       # Application status tracking model
│   │
│   ├── interfaces/
│   │   └── EligibilityChecker.java# Interface defining eligibility checking contracts
│   │
│   ├── service/
│   │   ├── EligibilityResult.java # Result object storing boolean status & failure reasons
│   │   ├── EligibilityService.java# Implements EligibilityChecker with matching logic
│   │   ├── JobService.java        # Manages ArrayList<Company> and ArrayList<Job>
│   │   └── PlacementService.java  # Manages ArrayList<Student>, applications & reports
│   │
│   └── main/
│       └── PlacementManagementSystem.java # Server entry point using built-in Java HttpServer
│
├── web/
│   ├── index.html                 # Main dashboard UI with title and student byline
│   ├── style.css                  # Clean, modern visual styling (CSS only)
│   └── app.js                     # Tab switching & REST API integration
│
├── compile_and_run.bat            # Windows batch script for automated compilation & startup
└── run.ps1                        # PowerShell launch script
```

---

## 💡 Key Java OOP Concepts Applied

1. **Classes & Objects**: Core entities (`Student`, `Company`, `Job`, `Application`, `EligibilityService`) instantiated in memory.
2. **Encapsulation**: Private instance variables paired with public getters and setters.
3. **Interfaces**: `interfaces.EligibilityChecker` interface implemented by `service.EligibilityService`.
4. **Packages**: Modular organization into `model`, `interfaces`, `service`, and `main`.
5. **Collections (`ArrayList`)**: Dynamic storage for lists of students, companies, jobs, and applications.
6. **Constructors**: Default, parameterized, and helper constructors.
7. **Conditionals & Loops**: Business logic for matching CGPA, Branch, Percentage, Arrears, and missing skills.

---

## 🚀 How to Run the Project

1. **Start the System**:
   Open PowerShell or Command Prompt in the project folder (`C:\Users\THANUSHRI K\.gemini\antigravity\scratch\college-placement-system`) and run:
   ```powershell
   .\run.ps1
   ```
   *or using Command Prompt:*
   ```cmd
   compile_and_run.bat
   ```

2. **Access the Web Dashboard**:
   Open any browser and navigate to:
   ```text
   http://localhost:8080
   ```

---

## 🎯 Main Modules Included

### 1. Student Profile & Eligibility
- Enter student details (Name, Reg No, Department, CGPA, Percentage, Arrears, Skills, Contact).
- Select any student to run **Automated Student-Job Eligibility Matching**.
- Displays **Eligible** or **Not Eligible** with exact failure reasons (e.g., *CGPA requirement not satisfied*, *Branch does not match*, *Required skills missing*).

### 2. Company & Job Management
- Register visiting companies and add job openings.
- Define criteria: Required Branch, Min CGPA, Min Percentage, Max Arrears, Required Technical Skills.

### 3. Placement Analytics
- Application tracking table (Applied, Shortlisted, Selected, Rejected).
- Status dropdown updates and simple placement report generation.
