package services;

import entities.BillSummary;

import java.time.LocalDateTime;

import entities.Appointment;
import entities.Bill;

// Builds read-only bill presentation objects.
public class BillSummaryService {
    // Converts a Bill entity to a flattened BillSummary view.
    public BillSummary getSummary(Bill bill){
        int billId = bill.getBillId();
        double amount = bill.getAmount();
        LocalDateTime genTime = bill.getGeneratedAt();
        
        Appointment appointment = bill.getAppointment();
        int appointmentId = appointment.getId();
        String doctor = appointment.getDoc().getName();
        String patient = appointment.getPat().getName();
        LocalDateTime startTime = appointment.getStartTime();
        LocalDateTime endTime = appointment.getEndTime();

        BillSummary bsum  = new BillSummary(billId, appointmentId, amount, doctor, patient, startTime, endTime, genTime);
        
        return bsum;
    } 
}
