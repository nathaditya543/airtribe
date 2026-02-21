package entities;

import java.time.LocalDateTime;

// Appointment entity linking doctor, patient, and booked time range.
public class Appointment {
    int id;
    Doctor doc;
    Patient pat;
    Bill bill;
    LocalDateTime startTime;
    LocalDateTime endTime;
    

    public Appointment(int id, Doctor doc, Patient pat, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.doc = doc;
        this.pat = pat;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    
    public int getId() {
        return id;
    }

    public Doctor getDoc() {
        return doc;
    }

    public Patient getPat() {
        return pat;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Links generated bill back to this appointment.
    public void setBill(Bill bill){
        this.bill = bill;
    }
}
