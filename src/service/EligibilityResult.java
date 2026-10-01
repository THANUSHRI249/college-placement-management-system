package service;

import java.util.ArrayList;
import java.util.List;

/**
 * EligibilityResult - Helper class to store matching output and specific failure reasons.
 */
public class EligibilityResult {
    private boolean eligible;
    private List<String> reasons;

    public EligibilityResult(boolean eligible, List<String> reasons) {
        this.eligible = eligible;
        this.reasons = reasons != null ? reasons : new ArrayList<>();
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public void addReason(String reason) {
        if (this.reasons == null) {
            this.reasons = new ArrayList<>();
        }
        this.reasons.add(reason);
    }
}
