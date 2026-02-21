package entities;

import java.time.LocalTime;

// Simple weekly-template style doctor schedule definition.
public class Schedule {
    final int doctorId;
    final LocalTime workStart;
    final LocalTime workEnd;
    final int slotMinutes;

    public Schedule(int doctorId, LocalTime workStart, LocalTime workEnd, int slotMinutes) {
        this.doctorId = doctorId;
        this.workStart = workStart;
        this.workEnd = workEnd;
        this.slotMinutes = slotMinutes;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public LocalTime getWorkStart() {
        return workStart;
    }

    public LocalTime getWorkEnd() {
        return workEnd;
    }

    public int getSlotMinutes() {
        return slotMinutes;
    }
}
