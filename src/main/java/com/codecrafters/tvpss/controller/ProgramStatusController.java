package com.codecrafters.tvpss.controller;

import com.codecrafters.tvpss.model.ProgramStatusModel;
import com.codecrafters.tvpss.model.ResourceRequestModel;
import com.codecrafters.tvpss.service.ProgramStatusService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ProgramStatusController {

    @Autowired
    private ProgramStatusService programStatusService;

    // Show the form for Program Status submission
    @GetMapping("/program-status")
    public String showForm(Model model) {
        model.addAttribute("programStatus", new ProgramStatusModel());
        return "program-status/program-status-form";
    }

    // Submit the Program Status form
    @PostMapping("/submitProgramStatus")
    public String submitProgramStatus(ProgramStatusModel programStatus, RedirectAttributes redirectAttributes) {
        try {
            String versionCriteria = buildVersionCriteria(programStatus);
            programStatus.setVersionCriteria(versionCriteria);

            programStatus.setStatus("Pending"); // Set initial status
            programStatus.setCreatedAt(LocalDateTime.now());
            programStatusService.saveProgramStatus(programStatus);

            redirectAttributes.addFlashAttribute("success", "Program status submitted successfully!");

            return "redirect:/program-status/success";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to submit program status: " + e.getMessage());
            return "redirect:/program-status";
        }
    }

    // Helper method to build the JSON string for version criteria
    private String buildVersionCriteria(ProgramStatusModel programStatus) {
        String v1Name = programStatus.getV1_name() != null ? programStatus.getV1_name() : "No";
        String v1Logo = programStatus.getV1_logo() != null ? programStatus.getV1_logo() : "No";
        String v1Studio = programStatus.getV1_studio() != null ? programStatus.getV1_studio() : "No";

        String v2Recording = programStatus.getV2_recording() != null ? programStatus.getV2_recording() : "No";
        String v2Upload = programStatus.getV2_upload() != null ? programStatus.getV2_upload() : "No";

        String v3RecordingOut = programStatus.getV3_recording_out() != null ? programStatus.getV3_recording_out() : "No";
        String v3Collaborate = programStatus.getV3_collaborate() != null ? programStatus.getV3_collaborate() : "No";

        String v4GreenScreen = programStatus.getV4_green_screen() != null ? programStatus.getV4_green_screen() : "No";

        return String.format("{\"v1_name\": \"%s\", \"v1_logo\": \"%s\", \"v1_studio\": \"%s\", " +
                        "\"v2_recording\": \"%s\", \"v2_upload\": \"%s\", \"v3_recording_out\": \"%s\", \"v3_collaborate\": \"%s\", " +
                        "\"v4_green_screen\": \"%s\"}",
                v1Name, v1Logo, v1Studio, v2Recording, v2Upload, v3RecordingOut, v3Collaborate, v4GreenScreen);
    }

    // Show the success page after form submission
    @GetMapping("/program-status/success")
    public String showSuccessPage() {
        return "program-status/program-status-submit-successful";
    }

    // Show all program statuses for the school admin (including feedback handling)
    @GetMapping("/program-status-list")
    public String viewProgramStatuses(Model model, HttpSession session) {
        String schoolCode = (String) session.getAttribute("schoolCode");
        List<ProgramStatusModel> programStatuses = programStatusService.findBySchoolCode(schoolCode);

        // Check each program status for feedback and replace null feedback with "Waiting for Feedback"
        for (ProgramStatusModel status : programStatuses) {
            if (status.getFeedback() == null || status.getFeedback().isEmpty()) {
                status.setFeedback("Waiting for Feedback");
            }
        }

        model.addAttribute("programStatuses", programStatuses);
        return "program-status/program-status-list";
    }

    @GetMapping("/manage-program-status")
    public String showManageProgramStatusPage(@RequestParam(defaultValue = "pending") String status, Model model) {

        try {
            // Add all counts for the status cards
            List<ProgramStatusModel> pendingRequests = programStatusService.findByStatus("pending");
            List<ProgramStatusModel> approvedRequests = programStatusService.findByStatus("approved");
            List<ProgramStatusModel> rejectedRequests = programStatusService.findByStatus("rejected");

            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("approvedRequests", approvedRequests);
            model.addAttribute("rejectedRequests", rejectedRequests);

            // Add the filtered requests based on status
            model.addAttribute("programStatuses", programStatusService.findByStatus(status.toLowerCase()));
            model.addAttribute("currentStatus", status.toLowerCase());

            return "program-status/manage-program-status";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while loading the requests.");
            return "error";
        }

    }

    @PostMapping("/manage-program-status/{id}/approve")
    public String approveRequest(@PathVariable String id,
                                 @RequestParam String feedback,
                                 RedirectAttributes redirectAttributes) {
        programStatusService.approveRequest(id, feedback);
        redirectAttributes.addFlashAttribute("message", "Request approved successfully");
        return "redirect:/manage-program-status";
    }

    @PostMapping("/manage-program-status/{id}/reject")
    public String rejectRequest(@PathVariable String id,
                                 @RequestParam String feedback,
                                 RedirectAttributes redirectAttributes) {
        programStatusService.approveRequest(id, feedback);
        redirectAttributes.addFlashAttribute("message", "Request approved successfully");
        return "redirect:/manage-program-status";
    }

// test commit
    @GetMapping("/program-status-report")
    public String showProgramStatusReportPage(Model model) {

        List<ProgramStatusModel> allRequests = programStatusService.getAllProgramStatuses();
        model.addAttribute("allRequests", allRequests);

        long totalRequests = allRequests.size();
        long approvedRequests = allRequests.stream().filter(r -> "Approved".equals(r.getStatus())).count();
        long rejectedRequests = allRequests.stream().filter(r -> "Rejected".equals(r.getStatus())).count();

        model.addAttribute("totalRequests", totalRequests);
        model.addAttribute("approvedRequests", approvedRequests);
        model.addAttribute("rejectedRequests", rejectedRequests);

        return "program-status/program-status-report";
    }
}
