package net.iaround.model.entity;

import java.io.Serializable;

/**
 * @author：liush on 2017/1/21 00:23
 */
public class SecretEntity implements Serializable {
    private int salary;
    private int house;
    private int car;
    private String school;
    private String company;
    private int occupation;

    public int getOccupation() {
        return occupation == -1 ? 0 : occupation;
    }

    public void setOccupation(int occupation) {
        this.occupation = occupation;
    }

    public int getSalary() {
        return salary == -1 ? 0 : salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getHouse() {
        return house == -1 ? 0 : house;
    }

    public void setHouse(int house) {
        this.house = house;
    }

    public int getCar() {
        return car == -1 ? 0 : car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
