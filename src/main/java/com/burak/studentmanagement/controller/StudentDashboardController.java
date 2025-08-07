package com.burak.studentmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentDashboardController {
    @GetMapping("")
    public String studentDashboard() {
        return "student/student-dashboard";
    }
}
