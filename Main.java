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
import services.ScheduleService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

// Console entry point with menu-driven workflows for core MediTrack use cases.
public class Main {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        // Single scanner for the full interactive session.
        Scanner scanner = new Scanner(System.in);

        DocService docService = new DocService();
        PatientService patientService = new PatientService();
        ScheduleService scheduleService = new ScheduleService();
        AppService appService = new AppService(docService, patientService, scheduleService);
        BillService billService = new BillService();
        BillSummaryService billSummaryService = new BillSummaryService();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt(scanner, "Choose an option: ");

            try {
                switch (choice) {
                    case 1:
                        addDoctor(scanner, docService, scheduleService);
                        break;
                    case 2:
                        addPatient(scanner, patientService);
                        break;
                    case 3:
                        addAppointment(scanner, docService, patientService, appService);
                        break;
                    case 4:
                        addBill(scanner, appService, billService);
                        break;
                    case 5:
                        payBill(scanner, billService);
                        break;
                    case 6:
                        viewDoctor(scanner, docService);
                        break;
                    case 7:
                        viewPatient(scanner, patientService);
                        break;
                    case 8:
                        viewAppointment(scanner, appService);
                        break;
                    case 9:
                        billMenu(scanner, billService, billSummaryService);
                        break;
                    case 0:
                        running = false;
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (IdNotFound e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Input error: " + e.getMessage());
            }

            System.out.println();
        }

        scanner.close();
    }

    // Prints top-level menu options.
    private static void printMainMenu() {
        System.out.println("================================");
        System.out.println("        MediTrack Main Menu");
        System.out.println("================================");
        System.out.println("1. Add Doctor");
        System.out.println("2. Add Patient");
        System.out.println("3. Add Appointment");
        System.out.println("4. Generate Bill");
        System.out.println("5. Pay Bill");
        System.out.println("6. View Doctor By ID");
        System.out.println("7. View Patient By ID");
        System.out.println("8. View Appointment By ID");
        System.out.println("9. Bill Menu");
        System.out.println("0. Exit");
        System.out.println("--------------------------------");
    }

    // Nested bill menu for summary-specific actions.
    private static void billMenu(Scanner scanner, BillService billService, BillSummaryService billSummaryService) {
        boolean inBillMenu = true;
        while (inBillMenu) {
            System.out.println("\n========== Bill Menu ==========");
            System.out.println("1. View Bill Summary By Bill ID");
            System.out.println("0. Back To Main Menu");
            System.out.println("-------------------------------");

            int subChoice = readInt(scanner, "Choose bill option: ");
            switch (subChoice) {
                case 1:
                    viewBillSummary(scanner, billService, billSummaryService);
                    break;
                case 0:
                    inBillMenu = false;
                    break;
                default:
                    System.out.println("Invalid bill menu option.");
            }
        }
    }

    // Collects doctor profile and schedule details.
    private static void addDoctor(Scanner scanner, DocService docService, ScheduleService scheduleService) {
        System.out.println("\n[Add Doctor]");
        int id = readInt(scanner, "Doctor ID: ");
        String name = readString(scanner, "Doctor Name: ");
        int exp = readInt(scanner, "Years of Experience: ");
        String spec = readString(scanner, "Specialization: ");
        double consultationFee = readDouble(scanner, "Consultation Fee: ");
        double ratePerMinute = readDouble(scanner, "Rate Per Minute: ");
        LocalTime workStart = readTime(scanner, "Work Start (HH:mm): ");
        LocalTime workEnd = readTime(scanner, "Work End (HH:mm): ");
        int slotMinutes = readInt(scanner, "Slot Duration (minutes): ");

        docService.AddDoc(exp, id, name, spec, consultationFee, ratePerMinute);
        scheduleService.setSchedule(id, workStart, workEnd, slotMinutes);
        System.out.println("Doctor added.");
    }

    // Collects patient details and stores a patient record.
    private static void addPatient(Scanner scanner, PatientService patientService) {
        System.out.println("\n[Add Patient]");
        int id = readInt(scanner, "Patient ID: ");
        String name = readString(scanner, "Patient Name: ");
        int age = readInt(scanner, "Age: ");
        BloodType bloodType = readBloodType(scanner);

        patientService.AddPatient(id, name, age, bloodType);
        System.out.println("Patient added.");
    }

    // Collects appointment details and creates validated booking.
    private static void addAppointment(
        Scanner scanner,
        DocService docService,
        PatientService patientService,
        AppService appService
    ) {
        System.out.println("\n[Add Appointment]");
        int id = readInt(scanner, "Appointment ID: ");
        int docId = readInt(scanner, "Doctor ID: ");
        int patId = readInt(scanner, "Patient ID: ");

        LocalDateTime startTime = readDateTime(scanner, "Start Time (yyyy-MM-dd HH:mm): ");
        LocalDateTime endTime = readDateTime(scanner, "End Time (yyyy-MM-dd HH:mm): ");

        Doctor doctor = docService.getDoctor(docId);
        Patient patient = patientService.getPatient(patId);

        appService.addApp(id, doctor, patient, startTime, endTime);
        System.out.println("Appointment added.");
    }

    // Generates bill from appointment.
    private static void addBill(Scanner scanner, AppService appService, BillService billService) {
        System.out.println("\n[Generate Bill]");
        int billId = readInt(scanner, "Bill ID: ");
        int appointmentId = readInt(scanner, "Appointment ID: ");

        Appointment appointment = appService.getAppointment(appointmentId);
        billService.addBill(billId, appointment, LocalDateTime.now());
        System.out.println("Bill generated.");
    }

    // Marks a bill as paid.
    private static void payBill(Scanner scanner, BillService billService) {
        System.out.println("\n[Pay Bill]");
        int billId = readInt(scanner, "Bill ID: ");
        Bill bill = billService.getBill(billId);
        billService.payBill(bill);
        System.out.println("Bill marked as paid.");
    }

    // Reads and prints doctor details.
    private static void viewDoctor(Scanner scanner, DocService docService) {
        System.out.println("\n[View Doctor]");
        int id = readInt(scanner, "Doctor ID: ");
        Doctor doctor = docService.getDoctor(id);

        System.out.println("Doctor ID: " + doctor.getId());
        System.out.println("Name: " + doctor.getName());
        System.out.println("Experience: " + doctor.getExp());
        System.out.println("Specialization: " + doctor.getSpec());
        System.out.println("Consultation Fee: " + doctor.getConsultationFee());
        System.out.println("Rate/Minute: " + doctor.getRatePerMinute());
    }

    // Reads and prints patient details.
    private static void viewPatient(Scanner scanner, PatientService patientService) {
        System.out.println("\n[View Patient]");
        int id = readInt(scanner, "Patient ID: ");
        Patient patient = patientService.getPatient(id);

        System.out.println("Patient ID: " + patient.getId());
        System.out.println("Name: " + patient.getName());
        System.out.println("Age: " + patient.getAge());
        System.out.println("Blood Type: " + patient.getBloodType());
    }

    // Reads and prints appointment details.
    private static void viewAppointment(Scanner scanner, AppService appService) {
        System.out.println("\n[View Appointment]");
        int id = readInt(scanner, "Appointment ID: ");
        Appointment appointment = appService.getAppointment(id);
        long duration = Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes();

        System.out.println("Appointment ID: " + appointment.getId());
        System.out.println("Doctor: " + appointment.getDoc().getName() + " (ID " + appointment.getDoc().getId() + ")");
        System.out.println("Patient: " + appointment.getPat().getName() + " (ID " + appointment.getPat().getId() + ")");
        System.out.println("Start: " + appointment.getStartTime());
        System.out.println("End: " + appointment.getEndTime());
        System.out.println("Duration (minutes): " + duration);
    }

    // Reads bill and prints flattened summary view.
    private static void viewBillSummary(Scanner scanner, BillService billService, BillSummaryService billSummaryService) {
        System.out.println("\n[View Bill Summary]");
        int billId = readInt(scanner, "Bill ID: ");

        Bill bill = billService.getBill(billId);
        BillSummary summary = billSummaryService.getSummary(bill);

        System.out.println("Bill ID: " + summary.getBillId());
        System.out.println("Appointment ID: " + summary.getAppointmentId());
        System.out.println("Doctor: " + summary.getDoctor());
        System.out.println("Patient: " + summary.getPatient());
        System.out.println("Start: " + summary.getStartTime());
        System.out.println("End: " + summary.getEndTime());
        System.out.println("Amount: " + summary.getAmount());
        System.out.println("Generated At: " + summary.getGenTime());
    }

    // Safe integer parsing with consistent validation error.
    private static int readInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected an integer.");
        }
    }

    // Safe decimal parsing with consistent validation error.
    private static double readDouble(Scanner scanner, String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected a decimal number.");
        }
    }

    // Reads a non-empty string value.
    private static String readString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty.");
        }
        return value;
    }

    // Parses local date-time in menu-supported format.
    private static LocalDateTime readDateTime(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date time must be in format yyyy-MM-dd HH:mm");
        }
    }

    // Parses local time for schedule input.
    private static LocalTime readTime(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Time must be in format HH:mm");
        }
    }

    // Parses one of the supported BloodType enum values.
    private static BloodType readBloodType(Scanner scanner) {
        System.out.print("Blood Type " + java.util.Arrays.toString(BloodType.values()) + ": ");
        String value = scanner.nextLine().trim().toUpperCase();
        try {
            return BloodType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid blood type.");
        }
    }
}
