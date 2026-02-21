package entities;

import java.time.LocalDateTime;

// Read-only view model used for showing bill details in UI/output.
public class BillSummary {
    final int billId;
    final int appointmentId;
    final double amount;
    final String doctor;
    final String patient;
    final LocalDateTime startTime;
    final LocalDateTime endTime;
    final LocalDateTime genTime;

    public BillSummary(int billId, int appointmentId, double amount, String doctor, String patient, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime genTime){
        this.billId = billId;
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.doctor = doctor;
        this.patient  = patient;
        this.startTime = startTime;
        this.endTime = endTime;
        this.genTime  = genTime;
    }

    public int getBillId() {
        return billId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPatient() {
        return patient;
    }

    public String getDoctor() {
        return doctor;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getGenTime() {
        return genTime;
    }
}
