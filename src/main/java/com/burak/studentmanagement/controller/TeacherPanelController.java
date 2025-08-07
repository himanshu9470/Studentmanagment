package com.burak.studentmanagement.controller;

import com.burak.studentmanagement.user.UserDto;
import com.burak.studentmanagement.service.StudentService;
import com.burak.studentmanagement.entity.Role;
import com.burak.studentmanagement.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/teacher")
public class TeacherPanelController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private RoleDao roleDao;

    @GetMapping("")
    public String teacherPanel() {
        return "teacher/teacher-panel";
    }

    @GetMapping("/add-student")
    public String addStudentForm() {
        return "teacher/add-student-form";
    }

    @PostMapping("/add-student")
    public String addStudentSubmit(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String firstName,
                                   @RequestParam String lastName,
                                   @RequestParam String email,
                                   Model model) {
        UserDto userDto = new UserDto();
        userDto.setUserName(username);
        userDto.setPassword(password);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        Role studentRole = roleDao.findRoleByName("ROLE_STUDENT");
        userDto.setRole(studentRole);
        try {
            studentService.save(userDto);
            model.addAttribute("message", "Student added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error adding student: " + e.getMessage());
        }
        return "teacher/add-student-form";
    }
}
