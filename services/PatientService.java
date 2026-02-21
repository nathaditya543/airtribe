package services;

import entities.BloodType;
import entities.Patient;
import exceptions.IdNotFound;
import java.util.ArrayList;
import java.util.List;

// Manages patient records and patient CSV persistence.
public class PatientService {
    private static final String FILE_PATH = "data/patients.csv";
    private static final String HEADER = "id,name,age,bloodType";
    ArrayList<Patient> patList = new ArrayList<>();

    public PatientService() {
        // Load persisted patients into memory at startup.
        loadFromCsv();
    }

    // Adds a patient record and persists the updated list.
    public void AddPatient(int id, String name, int age, BloodType bloodType) {
        Patient newPat = new Patient(id, name, age, bloodType);
        patList.add(newPat);
        saveToCsv();
    }

    // Returns patient by ID or throws when missing.
    public Patient getPatient(int id) {
        for (Patient patient : patList) {
            if (patient.getId() == id) {
                return patient;
            }
        }
        throw new IdNotFound("Patient with ID " + id + " not found");
    }

    // Hydrates in-memory list from persisted CSV rows.
    private void loadFromCsv() {
        List<String> lines = CsvStore.readDataLines(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] p = line.split(",", -1);
            if (p.length < 4) {
                continue;
            }
            int id = Integer.parseInt(p[0]);
            String name = p[1];
            int age = Integer.parseInt(p[2]);
            BloodType bloodType = BloodType.valueOf(p[3]);
            patList.add(new Patient(id, name, age, bloodType));
        }
    }

    // Persists in-memory patient list to CSV.
    private void saveToCsv() {
        List<String> rows = new ArrayList<>();
        for (Patient p : patList) {
            rows.add(
                p.getId() + "," +
                p.getName() + "," +
                p.getAge() + "," +
                p.getBloodType()
            );
        }
        CsvStore.writeAll(FILE_PATH, HEADER, rows);
    }
}
