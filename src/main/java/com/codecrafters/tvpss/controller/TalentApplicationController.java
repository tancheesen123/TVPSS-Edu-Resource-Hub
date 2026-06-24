package com.codecrafters.tvpss.controller;

import com.codecrafters.tvpss.dao.PostTalentDao;
import com.codecrafters.tvpss.model.TalentApplicationModel;
import com.codecrafters.tvpss.model.TalentPostModel;
import com.codecrafters.tvpss.model.UserProfileModel;
import com.codecrafters.tvpss.model.TalentPostCandidateModel;
import com.codecrafters.tvpss.model.InterviewModel;
import com.codecrafters.tvpss.service.TalentApplicationService;
import com.codecrafters.tvpss.service.UserProfileService;
import com.codecrafters.tvpss.service.InterviewService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TalentApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(TalentApplicationController.class);

    @Autowired
    private TalentApplicationService applicationService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private InterviewService interviewService;
    @Autowired
    private PostTalentDao postTalentDao;

    @GetMapping("/talent-form")
    public String showForm(Model model) {
        model.addAttribute("talentApplicationRequest", new TalentApplicationModel());
        return "/talent-application/update-talent-application-status";
    }

    @GetMapping("/talent-list")
    public String showTalentApplicationList(Model model) {
        return "/talent-application/manage-talent-application";
    }

    @GetMapping("/talentPost-form")
    public String showTalentPostForm(Model model) {
        model.addAttribute("talentPostRequest", new TalentPostModel());
        return "/talent-application/create-talent-post";
    }

    @GetMapping("/talentPost-form/update/{id}")
    public String showUpdateTalentPostForm(Model model,@PathVariable String id,
                                RedirectAttributes redirectAttributes) {
        try {
            TalentPostModel talentPost = applicationService.findById(id);
            model.addAttribute("talentPost", talentPost);
            redirectAttributes.addFlashAttribute("message", "Request rejected successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject request");
        }
        return "/talent-application/update-talent-post";
    }

    @PostMapping("/talentPost-form/delete/{id}")
    public String deleteTalentPost(Model model,@PathVariable int id,
                                           RedirectAttributes redirectAttributes) {
        try {
            TalentPostModel talentPost = applicationService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Request rejected successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject request");
        }
        return "redirect:/talentPost-list";
    }

    @GetMapping("/talentPost-list")
    public String showTalentPostList(Model model) {
        List<TalentPostModel> talentPostList = applicationService.getAllPostTalent();
        logger.debug("Talent Post List Size: {}", talentPostList.size());
        model.addAttribute("talentPostList", talentPostList);
        return "talent-application/manage-talent-post";
    }

    @GetMapping("/talentPost-list/open")
    public String showOpenTalentPostList(Model model) {
        List<TalentPostModel> talentPostList = applicationService.getAllOpenPostTalent();
        logger.debug("Talent Post List Size: {}", talentPostList.size());
        model.addAttribute("talentPostList", talentPostList);
        return "talent-application/manage-talent-post";
    }

    @GetMapping("/talentPostCandidate-list")
    public String showTalentPosCandidateList(Model model) {
        List<TalentPostCandidateModel> talentPostCandidateList = applicationService.getAllPostTalentCandidate();
        logger.debug("Talent Post List Size: {}", talentPostCandidateList.size());
        model.addAttribute("talentPostCandidateList", talentPostCandidateList);
        return "/talent-application/manage-talent-post-candidate";
    }

    @GetMapping("/dashboard/admin/talentPostCandidate-list/sort")
    public String sortAdminCandidateList(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "ptc.apply_date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        // Retrieve the sorted list of candidates based on the user's profile and sorting parameters
        List<TalentPostCandidateModel> talentPostCandidateList =
                applicationService.sortAllByUser(sortBy, sortOrder);

        // Add the list and sorting information to the model to be used in the view
        model.addAttribute("talentPostCandidateList", talentPostCandidateList);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortOrder", sortOrder);

        // Return the view name (ensure this matches your view path)
        return "/talent-application/manage-talent-post-candidate";
    }

    @GetMapping("/dashboard/officer/talentPostCandidate-list")
    public String sortOfficerCandidateList(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "ptc.apply_date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        // Retrieve the sorted list of candidates based on the user's profile and sorting parameters
        List<TalentPostCandidateModel> talentPostCandidateList =
                applicationService.sortAllByUser(sortBy, sortOrder);

        // Add the list and sorting information to the model to be used in the view
        model.addAttribute("talentPostCandidateList", talentPostCandidateList);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortOrder", sortOrder);

        // Return the view name (ensure this matches your view path)
        return "talent-application/talent-post-candidate-report";
    }

    @GetMapping("/dashboard/student/talentPostCandidate-list")
    public String showStudentCandidateList(HttpSession session,Model model) {
        String userName = (String) session.getAttribute("username");
        UserProfileModel userProfileModel = userProfileService.findByUsername(userName);
        List<TalentPostCandidateModel> talentPostCandidateList = applicationService.getAllPostTalentCandidateByUserProfileId(userProfileModel.getId());
        logger.debug("Talent Post List Size: {}", talentPostCandidateList.size());
        model.addAttribute("talentPostCandidateList", talentPostCandidateList);
        return "/interview/student-interview-list";
    }

    @GetMapping("/dashboard/student/talentPostCandidate-list/sort")
    public String sortCandidateList(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "ptc.apply_date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        // Retrieve the username from the session and get the associated user profile
        String userName = (String) session.getAttribute("username");
        UserProfileModel userProfileModel = userProfileService.findByUsername(userName);

        // Retrieve the sorted list of candidates based on the user's profile and sorting parameters
        List<TalentPostCandidateModel> talentPostCandidateList =
                applicationService.sortCandidateByUserProfileId(userProfileModel.getId(), sortBy, sortOrder);

        // Add the list and sorting information to the model to be used in the view
        model.addAttribute("talentPostCandidateList", talentPostCandidateList);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortOrder", sortOrder);

        // Return the view name (ensure this matches your view path)
        return "/interview/student-interview-list";
    }

    @PostMapping("/submitTalentRequest")
    public String submitForm(TalentApplicationModel request, Model model) {
        model.addAttribute("message", "Request submitted successfully!");
        return "/talent-application/manage-talent-application";
    }

    @PostMapping("/submitTalentPostRequest")
    public String submitTalentPostForm(TalentPostModel request, Model model) {
        // Process the request object as needed
        applicationService.addPost(request);
        model.addAttribute("message", "Request submitted successfully!");
        return "redirect:/talentPost-list";
    }

    @PostMapping("/submitEditTalentPost")
    public String submitEditTalentPostForm(TalentPostModel request, Model model) {
        // Process the request object as needed
        applicationService.updatePost(request);
        model.addAttribute("message", "Request submitted successfully!");
        return "redirect:/talentPost-list";
    }

    @GetMapping("/dashboard/student/talent-post/view-all")
    public String showAllPostList(Model model) {
        List<TalentPostModel> talentPostList = applicationService.getAllOpenPostTalent();
        logger.debug("Talent Post List Size: {}", talentPostList.size());
        model.addAttribute("talentPostList", talentPostList);
        return "/talent-application/student-view-all-post-list";
    }

    @GetMapping("/dashboard/student/talent-post/apply/{id}")
    public String showPostList(Model model,@PathVariable String id) {
        TalentPostModel talentPost = applicationService.findById(id);
        logger.debug("TalentPost {}", talentPost.getTalentName());
        model.addAttribute("talentPost", talentPost);
        return "/talent-application/student-apply-post-list";
    }

    @PostMapping("/submitAddPostCandidate")
    public String submitAddPostCandidateForm(HttpSession session, TalentPostModel request, Model model) {
        // Process the request object as needed
        String userName = (String) session.getAttribute("username");
        UserProfileModel userProfileModel = userProfileService.findByUsername(userName);
        long postIdLong = request.getId(); // Assuming getId() returns a long
        int postId = (int) postIdLong;
        InterviewModel interviewModel = new InterviewModel();
        int interview_id = interviewService.createInterview(interviewModel,postId, userName);

        applicationService.addPostCandidate(request,userProfileModel.getId(),interview_id);
        model.addAttribute("message", "Request submitted successfully!");
        return "redirect:/dashboard/student";
    }

    @PostMapping("/talentPostCandidate-list/{id}/approve")
    public String approvePostCandidate(@PathVariable int id,
                                   RedirectAttributes redirectAttributes) {
        String status = "approved";
        applicationService.updateStatusApprove(id, status);
        redirectAttributes.addFlashAttribute("message", "Request approved successfully");
        return "redirect:/talentPostCandidate-list";
    }

    @PostMapping("/talentPostCandidate-list/{id}/{idInterview}/reject")
    public String rejectPostCandidate(@PathVariable int id, @PathVariable int idInterview,
                                       RedirectAttributes redirectAttributes) {
        String status = "rejected";
        String feedback = "rejected";
        logger.debug("This is id and status {}", id);
        applicationService.updateStatusApprove(id, status);
        interviewService.rejectInterview(idInterview,status,feedback);
        redirectAttributes.addFlashAttribute("message", "Request approved successfully");
        return "redirect:/talentPostCandidate-list";
    }


}
