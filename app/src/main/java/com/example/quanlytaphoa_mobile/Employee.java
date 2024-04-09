package com.example.quanlytaphoa_mobile;
import java.io.Serializable;


public class Employee implements Serializable {
    private String id;
    private String name;
    private String chucvu;
    private int hoursWorked;
    private int salary;

    public Employee() {
        // Empty constructor required for Firebase
    }

    public Employee(String id, String name, String chucvu, int hoursWorked, int salary) {
        this.id = id;
        this.name = name;
        this.chucvu = chucvu;
        this.hoursWorked = hoursWorked;
        this.salary = salary;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChucvu() {
        return chucvu;
    }

    public void setChucvu(String chucvu) {
        this.chucvu = chucvu;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }


}
