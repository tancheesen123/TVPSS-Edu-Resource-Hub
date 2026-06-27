package com.codecrafters.tvpss.controller;

import com.codecrafters.tvpss.model.TalentPostModel;
import com.codecrafters.tvpss.model.UserProfileModel;
import com.codecrafters.tvpss.service.DashboardService;
import com.codecrafters.tvpss.service.TalentApplicationService;
import com.codecrafters.tvpss.service.UserProfileService;
import com.codecrafters.tvpss.model.Dashboard;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;

import java.util.List;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private static final String ATTR_DASHBOARD = "dashboard";

    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TalentApplicationService talentApplicationService;
    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/dashboard")
    public String defaultRedirect(Authentication authentication, HttpSession session) {
        if (authentication != null && authentication.getAuthorities() != null) {
            String username = authentication.getName();
            session.setAttribute("username", username);
            session.setAttribute("schoolCode", "");
            session.setAttribute("schoolName", "");
            loadSchoolInfo(username, session);
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority().replace("ROLE_", "");
                session.setAttribute("userRole", role);
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    return "redirect:/dashboard/admin";
                } else if (authority.getAuthority().equals("ROLE_OFFICER")) {
                    return "redirect:/dashboard/officer";
                } else if (authority.getAuthority().equals("ROLE_STUDENT")) {
                    return "redirect:/dashboard/student";
                }
            }
        }
        return "redirect:/login";
    }

    private void loadSchoolInfo(String username, HttpSession session) {
        try {
            jdbcTemplate.queryForObject(
                "SELECT school_code, school_name FROM auth WHERE username = ?",
                (rs, rowNum) -> {
                    String schoolCode = rs.getString("school_code");
                    String schoolName = rs.getString("school_name");
                    if (schoolCode != null) {
                        session.setAttribute("schoolCode", schoolCode);
                    }
                    if (schoolName != null) {
                        session.setAttribute("schoolName", schoolName);
                    }
                    return null;
                },
                username
            );
        } catch (DataAccessException e) {
            logger.error("Error fetching school info for username {}", username, e);
        }
    }

    @GetMapping("/dashboard/admin")
    public String adminDashboard(Model model) {
        Dashboard dashboard = dashboardService.getAdminDashboard();
        model.addAttribute(ATTR_DASHBOARD, dashboard);
        return "dashboard/dashboard-admin";
    }

    @GetMapping("/dashboard/officer")
    public String officerDashboard(Model model) {
        Dashboard dashboard = dashboardService.getOfficerDashboard();
        model.addAttribute(ATTR_DASHBOARD, dashboard);
        return "dashboard/dashboard-officer";
    }

    @GetMapping("/dashboard/student")
    public String studentDashboard(HttpSession session,Model model) {
        Dashboard dashboard = dashboardService.getStudentDashboard();
        List<TalentPostModel> threetalentPostList = talentApplicationService.getThreePostTalent();
        model.addAttribute("threetalentPostList", threetalentPostList);
        model.addAttribute(ATTR_DASHBOARD, dashboard);
        String userName = (String) session.getAttribute("username");
        UserProfileModel userProfileModel = userProfileService.findByUsername(userName);
        model.addAttribute("userProfile", userProfileModel);
        logger.debug("This is Username {}", userProfileModel.getAbout_me());
        return "dashboard/dashboard-student";
    }
}
