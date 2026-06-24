package com.codecrafters.tvpss.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.codecrafters.tvpss.service.UserProfileService;
import com.codecrafters.tvpss.model.UserProfileModel;

@Controller
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/candidate-request")
    public String showForm(Model model) {
        model.addAttribute("candidateRequest", new UserProfileModel());
        return "/candidate/candidate-application-list";
    }

    @GetMapping("/profile/{id}")
    public String showCandidateProfile(Model model, @PathVariable int id) {
        UserProfileModel userProfileModel = userProfileService.findById(id);
        model.addAttribute("userProfile", userProfileModel);
        return "user/user-profile";
    }

    @GetMapping("/student/profile")
    public String showStudentProfile(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("username");
        UserProfileModel userProfileModel = userProfileService.findByUsername(userName);
        model.addAttribute("userProfile", userProfileModel);
        model.addAttribute("isStudent", true);
        return "user/user-profile";
    }

    @GetMapping("/student/profile/edit/{id}")
    public String showEditStudentProfileForm(Model model, @PathVariable int id) {
        UserProfileModel userProfileModel = userProfileService.findById(id);
        model.addAttribute("userProfile", userProfileModel);
        return "user/edit-user-profile";
    }

    @PostMapping("/submitUpdateUserProfile")
    public String submitForm(UserProfileModel request, Model model) {
        // Process the request object as needed
        userProfileService.updateUserProfile(request);
        model.addAttribute("message", "Request submitted successfully!");
        return "redirect:/student/profile";
    }

}
