package services;

import entities.Appointment;
import entities.Doctor;
import entities.Schedule;
import exceptions.IdNotFound;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Manages doctor schedules and validates appointment time availability.
public class ScheduleService {
    private static final String FILE_PATH = "data/schedules.csv";
    private static final String HEADER = "doctorId,workStart,workEnd,slotMinutes";
    private final Map<Integer, Schedule> schedulesByDoctorId = new HashMap<>();

    public ScheduleService() {
        // Load persisted schedules into memory at startup.
        loadFromCsv();
    }

    // Creates or updates doctor schedule after basic validation.
    public void setSchedule(Schedule schedule) {
        if (schedule.getSlotMinutes() <= 0) {
            throw new IllegalArgumentException("Slot minutes must be positive.");
        }
        if (!schedule.getWorkEnd().isAfter(schedule.getWorkStart())) {
            throw new IllegalArgumentException("Work end must be after work start.");
        }
        schedulesByDoctorId.put(schedule.getDoctorId(), schedule);
        saveToCsv();
    }

    // Convenience overload for direct primitive input.
    public void setSchedule(int doctorId, LocalTime workStart, LocalTime workEnd, int slotMinutes) {
        setSchedule(new Schedule(doctorId, workStart, workEnd, slotMinutes));
    }

    // Returns schedule by doctor ID or throws when missing.
    public Schedule getSchedule(int doctorId) {
        Schedule schedule = schedulesByDoctorId.get(doctorId);
        if (schedule == null) {
            throw new IdNotFound("Schedule for doctor with ID " + doctorId + " not found");
        }
        return schedule;
    }

    // Validates slot boundaries, slot alignment, and conflict overlap.
    public void validateDoctorAvailability(Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Appointment end time must be after start time.");
        }
        if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
            throw new IllegalArgumentException("Appointment must be within one day.");
        }

        Schedule schedule = getSchedule(doctor.getId());
        LocalTime start = startTime.toLocalTime();
        LocalTime end = endTime.toLocalTime();

        if (start.isBefore(schedule.getWorkStart()) || end.isAfter(schedule.getWorkEnd())) {
            throw new IllegalArgumentException("Doctor is not available in this time range.");
        }

        long slotMinutes = schedule.getSlotMinutes();
        long fromWorkStartToStart = Duration.between(schedule.getWorkStart(), start).toMinutes();
        long fromWorkStartToEnd = Duration.between(schedule.getWorkStart(), end).toMinutes();
        long requestedDuration = Duration.between(start, end).toMinutes();

        if (fromWorkStartToStart % slotMinutes != 0 || fromWorkStartToEnd % slotMinutes != 0 || requestedDuration % slotMinutes != 0) {
            throw new IllegalArgumentException("Requested time does not match doctor's slot size.");
        }

        for (Appointment existing : doctor.getAppointments()) {
            boolean overlaps = startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime());
            if (overlaps) {
                throw new IllegalArgumentException("Doctor already has an appointment in this slot.");
            }
        }
    }

    // Hydrates schedule map from persisted CSV rows.
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

            int doctorId = Integer.parseInt(p[0]);
            LocalTime workStart = LocalTime.parse(p[1]);
            LocalTime workEnd = LocalTime.parse(p[2]);
            int slotMinutes = Integer.parseInt(p[3]);

            if (slotMinutes <= 0 || !workEnd.isAfter(workStart)) {
                continue;
            }
            schedulesByDoctorId.put(doctorId, new Schedule(doctorId, workStart, workEnd, slotMinutes));
        }
    }

    // Persists schedule map to CSV.
    private void saveToCsv() {
        List<String> rows = new ArrayList<>();
        for (Schedule schedule : schedulesByDoctorId.values()) {
            rows.add(
                schedule.getDoctorId() + "," +
                schedule.getWorkStart() + "," +
                schedule.getWorkEnd() + "," +
                schedule.getSlotMinutes()
            );
        }
        CsvStore.writeAll(FILE_PATH, HEADER, rows);
    }
}
