package services;

import entities.Doctor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Rule-based symptom matching service for doctor recommendation.
public class RecommendationService {
    private final DocService docService;
    // LinkedHashMap preserves insertion order so earlier rules have higher priority.
    private final Map<String, String> symptomRules = new LinkedHashMap<>();

    public RecommendationService(DocService docService) {
        this.docService = docService;
        // Seed with a minimal default rule set.
        loadDefaultRules();
    }

    // Adds or overrides a symptom keyword -> specialization rule.
    public void addRule(String symptomKeyword, String specialization) {
        symptomRules.put(symptomKeyword.toLowerCase(), specialization);
    }

    // Finds first matching specialization based on configured rules.
    public String recommendSpecialization(String symptoms) {
        String text = symptoms.toLowerCase();
        for (Map.Entry<String, String> rule : symptomRules.entrySet()) {
            if (text.contains(rule.getKey())) {
                return rule.getValue();
            }
        }
        return "General";
    }

    // Returns doctors for recommended specialization, fallback to General.
    public List<Doctor> recommendDoctors(String symptoms) {
        String specialization = recommendSpecialization(symptoms);
        List<Doctor> matches = docService.getDoctorsBySpec(specialization);
        if (!matches.isEmpty()) {
            return matches;
        }
        return docService.getDoctorsBySpec("General");
    }

    // Default symptom rules used for initial recommendations.
    private void loadDefaultRules() {
        addRule("chest", "Cardiology");
        addRule("heart", "Cardiology");
        addRule("skin", "Dermatology");
        addRule("rash", "Dermatology");
        addRule("headache", "Neurology");
        addRule("migraine", "Neurology");
        addRule("pregnan", "Gynecology");
        addRule("child", "Pediatrics");
        addRule("bone", "Orthopedics");
        addRule("joint", "Orthopedics");
        addRule("stomach", "Gastroenterology");
        addRule("fever", "General");
    }
}
