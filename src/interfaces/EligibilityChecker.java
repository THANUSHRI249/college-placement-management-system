package interfaces;

import model.Student;
import model.Job;
import service.EligibilityResult;

/**
 * Interface EligibilityChecker - Defines contract for evaluating student eligibility for jobs.
 * Demonstrates Interface concept in Java OOP.
 */
public interface EligibilityChecker {
    /**
     * Checks if a student satisfies all criteria for a given job.
     * @param student The student profile
     * @param job The job opportunity with criteria
     * @return true if student is eligible, false otherwise
     */
    boolean checkEligibility(Student student, Job job);

    /**
     * Detailed evaluation containing pass/fail status and exact reasons.
     * @param student The student profile
     * @param job The job opportunity
     * @return EligibilityResult object containing boolean status and reason list
     */
    EligibilityResult evaluateEligibility(Student student, Job job);
}
