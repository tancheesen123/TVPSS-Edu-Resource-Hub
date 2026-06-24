package com.codecrafters.tvpss.dao;

import com.codecrafters.tvpss.model.ResourceRequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.codecrafters.tvpss.model.TalentPostModel;
import com.codecrafters.tvpss.model.TalentPostCandidateModel;
import com.codecrafters.tvpss.model.InterviewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

@Repository
public class InterviewDao {

    private static final Logger logger = LoggerFactory.getLogger(InterviewDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createInterview(InterviewModel interview, int post_talent_id, String username) {
        String sql = "INSERT INTO interview (post_talent_id,username, time, date, feedback, status) VALUES (?, ?, ?, ?, ?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, post_talent_id);
            ps.setString(2, username);
            ps.setString(3, "-");
            ps.setString(4, "-");
            ps.setString(5, "-");
            ps.setString(6, "-");
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("id")) {
            return ((Number) keys.get("id")).intValue(); // Return the generated interview ID
        } else {
            throw new IllegalStateException("Failed to retrieve the generated ID.");
        }
    }


    public void addInterviewData(InterviewModel request) {
        String sql = "INSERT INTO interview (feedback, status, date, time, post_talent_id) " +
                "VALUES (?, ?, ?, ?,?)";

        jdbcTemplate.update(sql,
                "-",
                "pending",
                "-",
                "-",
                ""
        );
    }

    public void updatePostCandidateId(int interview_id, int post_talent_id) {

        String sql = "UPDATE interview SET post_talent_id=? WHERE id=?";
        try {
            jdbcTemplate.update(sql,
                    post_talent_id,
                    interview_id
            );
        } catch (Exception e) {
            logger.error("Error update interview table: ", e);
            // You can throw a custom exception or return an empty list, depending on your needs.
        }
    }

    public List<InterviewModel> findAll() {
        String sql = "SELECT * FROM interview ORDER BY date DESC";
        try {
            return jdbcTemplate.query(sql, new InterviewRowMapper());
        } catch (Exception e) {
            logger.error("Error fetching interview data: ", e);
        }
        return List.of();
    }

    public InterviewModel findById(int id) {
        String sql = "SELECT * FROM interview WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new InterviewRowMapper(), id);
        } catch (Exception e) {
            logger.error("Error fetching interview by ID: ", e);
        }
        return null;
    }

    public void save(InterviewModel interview) {
        String sql = "INSERT INTO interview (post_talent_id, time, date, feedback, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql,
                    interview.getPost_talent_id(),
                    interview.getTime(),
                    interview.getDate(),
                    interview.getFeedback(),
                    interview.getStatus()
            );
        } catch (Exception e) {
            logger.error("Error saving interview data: ", e);
        }
    }

    public void update(InterviewModel interview) {
        String sql = "UPDATE interview SET post_talent_id = ?, time = ?, date = ?, feedback = ?, status = ?, username = ? " +
                "WHERE id = ?";
        logger.debug("this is username {}", interview.getUsername());
        try {
            jdbcTemplate.update(sql,
                    interview.getPost_talent_id(),
                    interview.getTime(),
                    interview.getDate(),
                    interview.getFeedback(),
                    interview.getStatus(),
                    interview.getUsername(),
                    interview.getId()
            );
        } catch (Exception e) {
            logger.error("Error updating interview data: ", e);
        }
    }

    public void rejectInterview(int id, String status, String feedback) {
        String sql = "UPDATE interview SET feedback = ?, status = ? " +
                "WHERE id = ?";
        try {
            jdbcTemplate.update(sql,
                    feedback,
                    status,
                    id
            );
        } catch (Exception e) {
            logger.error("Error updating interview data: ", e);
        }
    }

    public void approveInterview(int id, String status, String feedback) {
        String sql = "UPDATE interview SET feedback = ?, status = ? " +
                "WHERE id = ?";
        try {
            jdbcTemplate.update(sql,
                    feedback,
                    status,
                    id
            );
        } catch (Exception e) {
            logger.error("Error updating interview data: ", e);
        }
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM interview WHERE id = ?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            logger.error("Error deleting interview by ID: ", e);
        }
    }

    private static class InterviewRowMapper implements RowMapper<InterviewModel> {
        @Override
        public InterviewModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            InterviewModel interview = new InterviewModel();
            interview.setId(rs.getInt("id"));
            interview.setPost_talent_id(rs.getInt("post_talent_id"));
            interview.setTime(rs.getString("time"));
            interview.setDate(rs.getString("date"));
            interview.setFeedback(rs.getString("feedback"));
            interview.setStatus(rs.getString("status"));
            interview.setUsername(rs.getString("username"));
            return interview;
        }
    }
}
