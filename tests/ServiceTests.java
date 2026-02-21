package tests;

import entities.Appointment;
import entities.Bill;
import entities.BillSummary;
import entities.BloodType;
import entities.Doctor;
import entities.Patient;
import exceptions.IdNotFound;
import services.AppService;
import services.BillService;
import services.BillSummaryService;
import services.DocService;
import services.PatientService;
import services.RecommendationService;
import services.ScheduleService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

// End-to-end service tests using local CSV files as backing storage.
public class ServiceTests {
    // Convenience wrapper if all tests are triggered manually.
    public void runAll() {
        testAddAndGetFlow();
        testNotFoundExceptions();
        testBillSummaryGeneration();
        testCsvPersistenceReload();
        testRecommendationService();
    }

    // Cleans persistent files to keep tests isolated and repeatable.
    private void resetData() {
        Path dataDir = Paths.get("data");
        if (!Files.exists(dataDir)) {
            return;
        }
        try {
            Files.deleteIfExists(dataDir.resolve("doctors.csv"));
            Files.deleteIfExists(dataDir.resolve("patients.csv"));
            Files.deleteIfExists(dataDir.resolve("appointments.csv"));
            Files.deleteIfExists(dataDir.resolve("bills.csv"));
            Files.deleteIfExists(dataDir.resolve("schedules.csv"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to clean test data files", e);
        }
    }

    // Verifies add/get flows for doctor, patient, appointment, and bill.
    public void testAddAndGetFlow() {
        resetData();

        DocService docService = new DocService();
        PatientService patientService = new PatientService();
        ScheduleService scheduleService = new ScheduleService();
        AppService appService = new AppService(docService, patientService, scheduleService);
        BillService billService = new BillService();

        docService.AddDoc(10, 1, "Dr. Lee", "Cardiology", 200.0, 2.5);
        scheduleService.setSchedule(1, java.time.LocalTime.of(9, 0), java.time.LocalTime.of(17, 0), 30);
        patientService.AddPatient(1, "Nina", 29, BloodType.O_P);

        Doctor doctor = docService.getDoctor(1);
        Patient patient = patientService.getPatient(1);

        LocalDateTime start = LocalDateTime.of(2026, 2, 14, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 14, 10, 30);
        appService.addApp(100, doctor, patient, start, end);

        Appointment appointment = appService.getAppointment(100);
        assertEquals(100, appointment.getId(), "Appointment ID mismatch");
        assertEquals("Dr. Lee", appointment.getDoc().getName(), "Doctor name mismatch");
        assertEquals("Nina", appointment.getPat().getName(), "Patient name mismatch");

        billService.addBill(500, appointment, LocalDateTime.of(2026, 2, 14, 11, 0));
        Bill bill = billService.getBill(500);
        assertEquals(500, bill.getBillId(), "Bill ID mismatch");
        assertTrue(bill.getAmount() > 0.0, "Bill amount must be > 0");

        billService.payBill(bill);
        assertTrue(bill.getStatus(), "Bill should be marked paid");
    }

    // Verifies ID lookup failures raise expected domain exception.
    public void testNotFoundExceptions() {
        resetData();

        DocService docService = new DocService();
        PatientService patientService = new PatientService();
        ScheduleService scheduleService = new ScheduleService();
        AppService appService = new AppService(docService, patientService, scheduleService);
        BillService billService = new BillService();

        assertThrows(() -> docService.getDoctor(999), "Doctor not-found should throw");
        assertThrows(() -> patientService.getPatient(999), "Patient not-found should throw");
        assertThrows(() -> appService.getAppointment(999), "Appointment not-found should throw");
        assertThrows(() -> billService.getBill(999), "Bill not-found should throw");
    }

    // Verifies bill summary projection values are correctly mapped.
    public void testBillSummaryGeneration() {
        resetData();

        DocService docService = new DocService();
        PatientService patientService = new PatientService();
        ScheduleService scheduleService = new ScheduleService();
        AppService appService = new AppService(docService, patientService, scheduleService);
        BillService billService = new BillService();
        BillSummaryService billSummaryService = new BillSummaryService();

        docService.AddDoc(8, 3, "Dr. Rao", "General", 150.0, 1.0);
        scheduleService.setSchedule(3, java.time.LocalTime.of(8, 0), java.time.LocalTime.of(16, 0), 10);
        patientService.AddPatient(7, "Mark", 35, BloodType.A_P);

        Appointment appointment = new Appointment(
            22,
            docService.getDoctor(3),
            patientService.getPatient(7),
            LocalDateTime.of(2026, 2, 14, 8, 0),
            LocalDateTime.of(2026, 2, 14, 8, 20)
        );
        appService.addApp(
            appointment.getId(),
            appointment.getDoc(),
            appointment.getPat(),
            appointment.getStartTime(),
            appointment.getEndTime()
        );

        billService.addBill(700, appService.getAppointment(22), LocalDateTime.of(2026, 2, 14, 9, 0));
        BillSummary summary = billSummaryService.getSummary(billService.getBill(700));

        assertEquals(700, summary.getBillId(), "Summary bill id mismatch");
        assertEquals(22, summary.getAppointmentId(), "Summary appointment id mismatch");
        assertEquals("Dr. Rao", summary.getDoctor(), "Summary doctor mismatch");
        assertEquals("Mark", summary.getPatient(), "Summary patient mismatch");
    }

    // Verifies CSV-backed persistence survives service re-instantiation.
    public void testCsvPersistenceReload() {
        resetData();

        DocService docService = new DocService();
        PatientService patientService = new PatientService();
        ScheduleService scheduleService = new ScheduleService();
        AppService appService = new AppService(docService, patientService, scheduleService);

        docService.AddDoc(12, 41, "Dr. West", "Neuro", 300.0, 3.0);
        scheduleService.setSchedule(41, java.time.LocalTime.of(13, 0), java.time.LocalTime.of(18, 0), 15);
        patientService.AddPatient(51, "Elena", 41, BloodType.B_N);
        appService.addApp(
            61,
            docService.getDoctor(41),
            patientService.getPatient(51),
            LocalDateTime.of(2026, 2, 14, 14, 0),
            LocalDateTime.of(2026, 2, 14, 14, 45)
        );

        DocService reloadedDocService = new DocService();
        PatientService reloadedPatientService = new PatientService();
        ScheduleService reloadedScheduleService = new ScheduleService();
        AppService reloadedAppService = new AppService(reloadedDocService, reloadedPatientService, reloadedScheduleService);

        assertEquals("Dr. West", reloadedDocService.getDoctor(41).getName(), "Doctor not persisted");
        assertEquals("Elena", reloadedPatientService.getPatient(51).getName(), "Patient not persisted");
        assertEquals(61, reloadedAppService.getAppointment(61).getId(), "Appointment not persisted");
        assertEquals(15, reloadedScheduleService.getSchedule(41).getSlotMinutes(), "Schedule not persisted");
    }

    // Verifies symptom rules and fallback doctor recommendation behavior.
    public void testRecommendationService() {
        resetData();

        DocService docService = new DocService();
        RecommendationService recommendationService = new RecommendationService(docService);

        docService.AddDoc(9, 101, "Dr. Cardio", "Cardiology", 200.0, 2.0);
        docService.AddDoc(7, 102, "Dr. General", "General", 120.0, 1.0);

        String spec = recommendationService.recommendSpecialization("Chest pain and heart discomfort");
        assertEquals("Cardiology", spec, "Specialization recommendation mismatch");

        java.util.List<Doctor> doctors = recommendationService.recommendDoctors("severe chest pain");
        assertEquals(1, doctors.size(), "Expected one cardiology doctor");
        assertEquals("Dr. Cardio", doctors.get(0).getName(), "Doctor recommendation mismatch");

        java.util.List<Doctor> fallbackDoctors = recommendationService.recommendDoctors("unknown symptom text");
        assertEquals("Dr. General", fallbackDoctors.get(0).getName(), "Fallback doctor recommendation mismatch");
    }

    // Minimal assertion helpers for dependency-free testing.
    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    // Integer equality assertion.
    private void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }

    // String equality assertion.
    private void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }

    // Asserts that an IdNotFound exception is thrown by action.
    private void assertThrows(Runnable action, String message) {
        try {
            action.run();
            throw new AssertionError(message + " (no exception thrown)");
        } catch (IdNotFound expected) {
        }
    }
}
