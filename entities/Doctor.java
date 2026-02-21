package entities;

import java.util.ArrayList;
import java.util.List;

// Doctor entity with profile, fees, and booked appointments.
public class Doctor extends Person {
    // Internal mutable appointment storage managed by services.
    ArrayList<Appointment> appList = new ArrayList<>();

    int exp;
    String spec;
    double consultationFee, ratePerMinute;

    public Doctor(int exp, int id, String name, String spec, double consultationFee, double ratePerMinute){
        super(id, name);
        this.exp = exp;
        this.id = id;
        this.spec = spec;
        this.consultationFee  = consultationFee;
        this.ratePerMinute = ratePerMinute;
    }

    public int getExp(){
        return exp;
    }

    public String getSpec(){
        return spec;
    }

    // Adds a newly created appointment for this doctor.
    public void addApp(Appointment appointment){
        appList.add(appointment);
    }

    // Returns a defensive shallow copy so callers cannot mutate appList directly.
    public List<Appointment> getAppointments() {
        return new ArrayList<>(appList);
    }

    // Fixed base fee charged per appointment.
    public double getConsultationFee(){
        return  consultationFee;
    }

    // Variable fee charged per minute.
    public double getRatePerMinute(){
        return ratePerMinute;
    }
}
