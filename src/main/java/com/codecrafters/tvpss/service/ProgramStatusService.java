package com.codecrafters.tvpss.service;

import com.codecrafters.tvpss.dao.ProgramStatusDao;
import com.codecrafters.tvpss.model.ProgramStatusModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProgramStatusService {

    @Autowired
    private ProgramStatusDao programStatusDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ProgramStatusModel> findByStatus(String status) {
        try {
            return programStatusDao.findByStatus(status.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveProgramStatus(ProgramStatusModel programStatus) {
        programStatus.setCreatedAt(LocalDateTime.now()); // Set current time for createdAt
        programStatusDao.save(programStatus);
    }

    public List<ProgramStatusModel> getAllProgramStatuses() {
        return programStatusDao.findAll();
    }

    public void approveRequest(String id, String feedback) {
        ProgramStatusModel request = programStatusDao.findById(Long.parseLong(id));
        request.setStatus("Approved");
        request.setFeedback(feedback);
        programStatusDao.update(request);
    }

    public void rejectRequest(String id, String feedback) {
        ProgramStatusModel request = programStatusDao.findById(Long.parseLong(id));
        request.setStatus("Rejected");
        request.setFeedback(feedback);
        programStatusDao.update(request);
    }

    public List<ProgramStatusModel> findBySchoolCode(String schoolCode) {
        String sql = "SELECT * FROM program_status WHERE school_code = ?";
        return jdbcTemplate.query(sql, new Object[]{schoolCode}, (rs, rowNum) -> {
            ProgramStatusModel programStatus = new ProgramStatusModel();
            programStatus.setId(rs.getLong("id"));
            programStatus.setSchoolCode(rs.getString("school_code"));
            programStatus.setSchoolName(rs.getString("school_name"));
            programStatus.setVersionCriteria(rs.getString("version_criteria"));
            programStatus.setYoutubeLink(rs.getString("youtube_link"));
            programStatus.setVersion(rs.getInt("version"));
            programStatus.setStatus(rs.getString("status"));
            programStatus.setFeedback(rs.getString("feedback"));

            return programStatus;
        });
    }

}
