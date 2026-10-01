package service;

import interfaces.EligibilityChecker;
import model.Student;
import model.Job;

import java.util.ArrayList;
import java.util.List;

/**
 * EligibilityService Class - Implements EligibilityChecker interface.
 * Contains core logic to match student criteria against job requirements.
 * Demonstrates Interfaces, Conditionals, Loops, and Collections.
 */
public class EligibilityService implements EligibilityChecker {

    @Override
    public boolean checkEligibility(Student student, Job job) {
        EligibilityResult result = evaluateEligibility(student, job);
        return result.isEligible();
    }

    @Override
    public EligibilityResult evaluateEligibility(Student student, Job job) {
        List<String> reasons = new ArrayList<>();

        if (student == null || job == null) {
            reasons.add("Invalid student or job data");
            return new EligibilityResult(false, reasons);
        }

        // 1. CGPA Check
        if (student.getCgpa() < job.getMinCgpa()) {
            reasons.add("CGPA requirement not satisfied (Min required: " + job.getMinCgpa() + ", Student: " + student.getCgpa() + ")");
        }

        // 2. Department / Branch Check
        String reqBranch = job.getRequiredBranch();
        if (reqBranch != null && !reqBranch.trim().isEmpty() && 
            !reqBranch.equalsIgnoreCase("ANY") && !reqBranch.equalsIgnoreCase("ALL")) {
            
            String studentDept = student.getDepartment() != null ? student.getDepartment().trim() : "";
            boolean branchMatches = studentDept.equalsIgnoreCase(reqBranch.trim()) ||
                                   studentDept.toLowerCase().contains(reqBranch.trim().toLowerCase()) ||
                                   reqBranch.trim().toLowerCase().contains(studentDept.toLowerCase());
            
            if (!branchMatches) {
                reasons.add("Branch does not match (Required: " + reqBranch + ", Student: " + student.getDepartment() + ")");
            }
        }

        // 3. Percentage Check
        if (student.getPercentage() < job.getMinPercentage()) {
            reasons.add("Percentage requirement not satisfied (Min required: " + job.getMinPercentage() + "%, Student: " + student.getPercentage() + "%)");
        }

        // 4. Maximum Arrears Check
        if (student.getArrears() > job.getMaxArrears()) {
            reasons.add("Arrears requirement not satisfied (Max allowed: " + job.getMaxArrears() + ", Student: " + student.getArrears() + ")");
        }

        // 5. Technical Skills Check
        List<String> jobSkills = job.getRequiredSkills();
        List<String> studentSkills = student.getSkills();
        
        if (jobSkills != null && !jobSkills.isEmpty()) {
            List<String> missingSkills = new ArrayList<>();
            for (String requiredSkill : jobSkills) {
                if (requiredSkill == null || requiredSkill.trim().isEmpty()) continue;
                
                boolean hasSkill = false;
                if (studentSkills != null) {
                    for (String sSkill : studentSkills) {
                        if (sSkill != null && sSkill.trim().equalsIgnoreCase(requiredSkill.trim())) {
                            hasSkill = true;
                            break;
                        }
                    }
                }
                
                if (!hasSkill) {
                    missingSkills.add(requiredSkill.trim());
                }
            }
            
            if (!missingSkills.isEmpty()) {
                if (missingSkills.size() == 1) {
                    reasons.add("Required skill missing: " + missingSkills.get(0));
                } else {
                    reasons.add("Required skills missing: " + String.join(", ", missingSkills));
                }
            }
        }

        boolean isEligible = reasons.isEmpty();
        return new EligibilityResult(isEligible, reasons);
    }
}
