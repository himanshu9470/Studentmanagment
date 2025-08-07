package com.burak.studentmanagement.controller;

import com.burak.studentmanagement.entity.Student;
import com.burak.studentmanagement.entity.Teacher;
import com.burak.studentmanagement.service.StudentService;
import com.burak.studentmanagement.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/reset-password")
    public String showResetForm() {
        return "login/reset-password-form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String username, @RequestParam String newPassword, Model model) {
        Student student = studentService.findByStudentName(username);
        Teacher teacher = teacherService.findByTeacherName(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean updated = false;
        if (student != null) {
            student.setPassword(encoder.encode(newPassword));
            studentService.save(student);
            updated = true;
        }
        if (teacher != null) {
            teacher.setPassword(encoder.encode(newPassword));
            teacherService.save(teacher);
            updated = true;
        }
        if (updated) {
            model.addAttribute("message", "Password reset successful. You can now log in.");
        } else {
            model.addAttribute("error", "Username not found.");
        }
        return "login/reset-password-form";
    }
}
