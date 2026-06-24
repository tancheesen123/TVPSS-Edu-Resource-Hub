package com.codecrafters.tvpss.service;

import com.codecrafters.tvpss.dao.InterviewDao;
import com.codecrafters.tvpss.model.InterviewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterviewService {

    @Autowired
    private InterviewDao interviewDao;

    private final List<InterviewModel> interviewRequests = new ArrayList<>();

    public void addInterviewData() {
        interviewDao.addInterviewData();
    }

    public int createInterview(int post_talent_id, String username) {
        return interviewDao.createInterview(post_talent_id, username);
    }

    public void  updatePostCandidateId(int interview_id, int post_talent_candidate_id) {
        interviewDao.updatePostCandidateId(interview_id, post_talent_candidate_id);
    }

    public List<InterviewModel> getAllInterviews() {
        return interviewDao.findAll();
    }

    public InterviewModel getInterviewById(int id) {
        return interviewDao.findById(id);
    }

    public void addInterview(InterviewModel interview) {
        interviewDao.save(interview);
    }

    public void updateInterview(InterviewModel interview) {
        interviewDao.update(interview);
    }

    public void rejectInterview(int id, String status, String feedback) {
        interviewDao.rejectInterview(id,status,feedback);
    }

    public void approveInterview(int id, String status, String feedback) {
        interviewDao.approveInterview(id,status,feedback);
    }
    public void deleteInterview(int id) {
        interviewDao.deleteById(id);
    }
}
