// EventActivitiesDao.java
package com.codecrafters.tvpss.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.codecrafters.tvpss.model.EventActivity;

@Repository

public class EventActivitiesDao {

    private static final Logger logger = LoggerFactory.getLogger(EventActivitiesDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<EventActivity> findAll() {
        String sql = "SELECT * FROM event_activities ORDER BY id ASC";
        try {
            return jdbcTemplate.query(sql, new EventActivityRowMapper());
        } catch (Exception e) {
            logger.error("Error fetching all event activities: ", e);
            return List.of();
        }
    }

    public List<EventActivity> getAllEventActivities() {
    String sql = "SELECT * FROM event_activities ORDER BY date";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(EventActivity.class));
}

public EventActivity findById(Long id) {
    String sql = "SELECT * FROM event_activities WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(EventActivity.class), id);
}

    public void addEventActivity(EventActivity eventActivity) {
        String sql = "INSERT INTO event_activities (title, image_url, description, date, time, location, details, participants_count, rating, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql,
                    eventActivity.getTitle(),
                    eventActivity.getImageUrl(),
                    eventActivity.getDescription(),
                    eventActivity.getDate(),
                    eventActivity.getTime(),
                    eventActivity.getLocation(),
                    eventActivity.getDetails(),
                    eventActivity.getParticipantsCount(),
                    eventActivity.getRating(),
                    eventActivity.getStatus());
        } catch (Exception e) {
            logger.error("Error adding event activity: ", e);
        }
    }

    public void updateEventActivity(EventActivity eventActivity) {
        String sql = "UPDATE event_activities SET title = ?, image_url = ?, description = ?, date = ?, time = ?, location = ?, details = ?, participants_count = ?, rating = ?, status = ? WHERE id = ?";
        try {
            jdbcTemplate.update(sql,
                    eventActivity.getTitle(),
                    eventActivity.getImageUrl(),
                    eventActivity.getDescription(),
                    eventActivity.getDate(),
                    eventActivity.getTime(),
                    eventActivity.getLocation(),
                    eventActivity.getDetails(),
                    eventActivity.getParticipantsCount(),
                    eventActivity.getRating(),
                    eventActivity.getStatus(),
                    eventActivity.getId());
        } catch (Exception e) {
            logger.error("Error updating event activity: ", e);
        }
    }

@Transactional
public void deleteById(Long id) {
    String sql = "DELETE FROM event_activities WHERE id = ?";
    jdbcTemplate.update(sql, id);
}

    private static class EventActivityRowMapper implements RowMapper<EventActivity> {
    @Override
    public EventActivity mapRow(ResultSet rs, int rowNum) throws SQLException {
        EventActivity eventActivity = new EventActivity();
        eventActivity.setId(rs.getLong("id"));
        eventActivity.setTitle(rs.getString("title"));
        eventActivity.setImageUrl(rs.getString("image_url"));
        eventActivity.setDescription(rs.getString("description"));
        eventActivity.setDate(rs.getDate("date"));
        eventActivity.setTime(rs.getString("time"));
        eventActivity.setLocation(rs.getString("location"));
        eventActivity.setDetails(rs.getString("details"));
        eventActivity.setParticipantsCount(rs.getInt("participants_count"));
        eventActivity.setRating(rs.getDouble("rating"));
        eventActivity.setStatus(rs.getString("status"));
        return eventActivity;
    }
}

    public void save(EventActivity event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }
}
