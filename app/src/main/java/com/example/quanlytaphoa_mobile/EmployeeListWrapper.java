package com.example.quanlytaphoa_mobile;

import java.io.Serializable;
import java.util.List;

public class EmployeeListWrapper implements Serializable {
    private List<Employee> employeeList;

    public EmployeeListWrapper(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }
}
