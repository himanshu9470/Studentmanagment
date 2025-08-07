package com.burak.studentmanagement.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.burak.studentmanagement.dao.RoleDao;
import com.burak.studentmanagement.entity.Role;
import com.burak.studentmanagement.service.StudentService;
import com.burak.studentmanagement.service.TeacherService;
import com.burak.studentmanagement.user.UserDto;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private RoleDao roleDao;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }    
    
    @GetMapping("/showRegistrationForm")
    public String showRegistrationForm(Model theModel) {
        theModel.addAttribute("userDto", new UserDto());        
        return "registration/registration-form";
    }
    
    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("userDto") UserDto user, 
            BindingResult theBindingResult, 
            @RequestParam(value="role") String roleName, 
            Model theModel) {
        
        if (theBindingResult.hasErrors()) {
            return "registration/registration-form";
        }
        
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Role role = roleDao.findRoleByName(roleName);
        if (role == null) {
            theModel.addAttribute("registrationError", "Invalid role specified");
            return "registration/registration-form";
        }
        user.setRole(role);
        
        if(roleName.equals("ROLE_STUDENT")) {
            if(studentService.findByStudentName(user.getUserName()) != null) {
                theModel.addAttribute("registrationError", "Username already exists");
                return "registration/registration-form";
            }
            studentService.save(user);
        } else if(roleName.equals("ROLE_TEACHER")) {
            if(teacherService.findByTeacherName(user.getUserName()) != null) {
                theModel.addAttribute("registrationError", "Username already exists");
                return "registration/registration-form";
            }
            teacherService.save(user);
        } else {
            theModel.addAttribute("registrationError", "Invalid role specified");
            return "registration/registration-form";
        }
        
        return "registration/registration-confirmation";
    }
}