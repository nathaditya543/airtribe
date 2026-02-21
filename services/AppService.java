package services;

import entities.*;
import exceptions.IdNotFound;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Manages appointment lifecycle and appointment CSV persistence.
public class AppService {
    private static final String FILE_PATH = "data/appointments.csv";
    private static final String HEADER = "id,doctorId,patientId,startTime,endTime";
    private final DocService docService;
    private final PatientService patientService;
    private final ScheduleService scheduleService;
    ArrayList<Appointment> appList = new ArrayList<>();

    public AppService(DocService docService, PatientService patientService, ScheduleService scheduleService) {
        this.docService = docService;
        this.patientService = patientService;
        this.scheduleService = scheduleService;
        // Load persisted appointments after dependent services are ready.
        loadFromCsv();
    }

    // Adds appointment after schedule validation, then persists changes.
    public void addApp(int id, Doctor doc, Patient pat, LocalDateTime startTime, LocalDateTime endTime){
        scheduleService.validateDoctorAvailability(doc, startTime, endTime);
        Appointment newApp  = new Appointment(id, doc, pat, startTime, endTime);
        appList.add(newApp);
        pat.addApp(newApp);
        doc.addApp(newApp);
        saveToCsv();
    }

    // Returns appointment by ID or throws when missing.
    public Appointment getAppointment(int id){
        for(Appointment appointment: appList){
            if(appointment.getId() == id)
                return appointment;
        }
        throw new IdNotFound("Appointment with ID " + id + " not found");
    }

    // Hydrates appointments from CSV and re-links doctor/patient references.
    private void loadFromCsv() {
        List<String> lines = CsvStore.readDataLines(FILE_PATH);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] p = line.split(",", -1);
            if (p.length < 5) {
                continue;
            }

            int id = Integer.parseInt(p[0]);
            int doctorId = Integer.parseInt(p[1]);
            int patientId = Integer.parseInt(p[2]);
            LocalDateTime startTime = LocalDateTime.parse(p[3]);
            LocalDateTime endTime = LocalDateTime.parse(p[4]);

            try {
                Doctor doc = docService.getDoctor(doctorId);
                Patient pat = patientService.getPatient(patientId);
                Appointment app = new Appointment(id, doc, pat, startTime, endTime);
                appList.add(app);
                pat.addApp(app);
                doc.addApp(app);
            } catch (IdNotFound ignored) {
                // Skip dangling rows if doctor/patient records are missing.
            }
        }
    }

    // Persists in-memory appointment list to CSV.
    private void saveToCsv() {
        List<String> rows = new ArrayList<>();
        for (Appointment a : appList) {
            rows.add(
                a.getId() + "," +
                a.getDoc().getId() + "," +
                a.getPat().getId() + "," +
                a.getStartTime() + "," +
                a.getEndTime()
            );
        }
        CsvStore.writeAll(FILE_PATH, HEADER, rows);
    }
}
