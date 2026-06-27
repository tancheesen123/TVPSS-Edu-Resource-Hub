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


    private static final String KEY_MESSAGE = "message";
    private static final String REDIRECT_CANDIDATE_LIST = "redirect:/talentPostCandidate-list";

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
        model.addAttribute(KEY_MESSAGE, "Request submitted successfully!");
        return REDIRECT_CANDIDATE_LIST;
    }
    @PostMapping("/admin/interview/{id}/approve")
    public String approveInterview(@PathVariable int id,
                                 @RequestParam String feedback,
                                 RedirectAttributes redirectAttributes) {
        String status = "approved";
        interviewService.approveInterview(id, status, feedback);
        redirectAttributes.addFlashAttribute(KEY_MESSAGE, "Request approved successfully");
        return REDIRECT_CANDIDATE_LIST;
    }

    @PostMapping("/admin/interview/{id}/reject")
    public String rejectInterview(@PathVariable int id,
                                 @RequestParam String feedback,
                                 RedirectAttributes redirectAttributes) {
        String status = "rejected";
        interviewService.rejectInterview(id, status, feedback);
        redirectAttributes.addFlashAttribute(KEY_MESSAGE, "Request approved successfully");
        return REDIRECT_CANDIDATE_LIST;
    }

}
