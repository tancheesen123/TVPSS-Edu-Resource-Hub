package com.codecrafters.tvpss.controller;

import com.codecrafters.tvpss.model.InterviewModel;
import com.codecrafters.tvpss.service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InterviewController {


    @Autowired
    private InterviewService interviewService;

    @GetMapping("/admin/interview/{id}")
    public String showForm(Model model,@PathVariable String id,
                           @RequestParam(defaultValue = "no") String create) {
        int interviewId = Integer.parseInt(id);
        InterviewModel interviewModel = interviewService.getInterviewById(interviewId);
        model.addAttribute("interview", interviewModel);
        model.addAttribute("create", create);
        return "/interview/interview-form";
    }

    @GetMapping("/interview-list")
    public String showInterviewList(Model model) {
        return "/interview/student-interview-list";
    }

    @PostMapping("/submitUpdateInterview")
    public String submitForm(InterviewModel request, Model model) {
        // Process the request object as needed
        interviewService.updateInterview(request);
        model.addAttribute("message", "Request submitted successfully!");
        return "redirect:/talentPostCandidate-list";
    }
    @PostMapping("/admin/interview/{id}/approve")
    public String approveInterview(@PathVariable int id,
                                 @RequestParam String feedback,
                                 RedirectAttributes redirectAttributes) {
        String status = "approved";
        interviewService.approveInterview(id, status, feedback);
        redirectAttributes.addFlashAttribute("message", "Request approved successfully");
        return "redirect:/talentPostCandidate-list";
    }

    @PostMapping("/admin/interview/{id}/reject")
    public String rejectInterview(@PathVariable int id,
                                 @RequestParam String feedback,
                                 RedirectAttributes redirectAttributes) {
        String status = "rejected";
        interviewService.rejectInterview(id, status, feedback);
        redirectAttributes.addFlashAttribute("message", "Request approved successfully");
        return "redirect:/talentPostCandidate-list";
    }

}
