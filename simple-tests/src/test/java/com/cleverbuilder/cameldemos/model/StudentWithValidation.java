package com.cleverbuilder.cameldemos.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * Created by tdonohue on 05/06/2018.
 */
public class StudentWithValidation {

    private String firstName;
    private String lastName;

    @NotNull
    private String grade;

    @AssertTrue
    private boolean graduated;

    public StudentWithValidation() {
    }

    public StudentWithValidation(String firstName, String lastName, String grade) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean isGraduated() {
        return graduated;
    }

    public void setGraduated(boolean graduated) {
        this.graduated = graduated;
    }

    @Override
    public String toString() {
        return "StudentWithValidation{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }
}
