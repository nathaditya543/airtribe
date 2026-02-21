package entities;

import java.util.ArrayList;

// Patient entity with demographics and assigned appointments.
public class Patient extends Person{
    // Internal appointment list managed by services.
    ArrayList<Appointment> appList = new ArrayList<>();

    int age;
    BloodType bloodType;
    String gender;
    double heightcm, weightkg;


    public Patient(int id, String name, int age, BloodType bloodType) {
        super(id, name);
        this.age = age;
        this.bloodType = bloodType;
    }


    public int getAge() {
        return age;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public String getGender() {
        return gender;
    }

    public double getHeightCm() {
        return heightcm;
    }

    public double getWeightKg() {
        return weightkg;
    }

    // Registers appointment association for this patient.
    public void addApp(Appointment appointment){
        appList.add(appointment);
    }
}
