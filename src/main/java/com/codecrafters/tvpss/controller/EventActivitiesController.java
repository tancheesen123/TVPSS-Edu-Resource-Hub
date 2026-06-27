package com.codecrafters.tvpss.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.codecrafters.tvpss.model.EventActivity;
import com.codecrafters.tvpss.service.EventActivitiesService;

@Controller
public class EventActivitiesController {

    private static final String ATTR_EVENTS = "events";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_MESSAGE = "message";

    @Autowired
    private EventActivitiesService eventActivitiesService;

    @GetMapping("/event-manage")
    public String showEventForm(Model model) {
        model.addAttribute("eventActivity", new EventActivity());
        return "event-activities/manage-event";
    }

    @PostMapping("/save-event")
    public ResponseEntity<List<EventActivity>> saveEvent(@ModelAttribute EventActivity eventActivity) {
        eventActivitiesService.saveEventActivity(eventActivity);
        List<EventActivity> updatedEvents = eventActivitiesService.getAllEventActivities();
        return ResponseEntity.ok(updatedEvents);
    }

    @GetMapping("/get-all-events")
public ResponseEntity<List<EventActivity>> getAllEvents() {
    List<EventActivity> events = eventActivitiesService.getAllEventActivities();
    return ResponseEntity.ok(events);
}

    @GetMapping("/event-list")
    public String showEventList(Model model) {
        List<EventActivity> events = eventActivitiesService.getAllEventActivities();
        model.addAttribute(ATTR_EVENTS, events);
        return "event-list";
    }

    @GetMapping("/event-activities/manage-event")
    public String manageEvents(Model model) {
        List<EventActivity> events = eventActivitiesService.getAllEventActivities();
        model.addAttribute(ATTR_EVENTS, events);
        return "event-activities/manage-event";
    }

    @GetMapping("/event-activities/edit-event/{eventId}")
    public String editEvent(@PathVariable Long eventId, Model model) {
        EventActivity event = eventActivitiesService.getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found for ID: " + eventId);
        }
        model.addAttribute("event", event);
        return "event-activities/edit-event";
    }    

    @GetMapping("/event-activities/get-event/{eventId}")
public ResponseEntity<EventActivity> getEvent(@PathVariable Long eventId) {
    EventActivity event = eventActivitiesService.getEventById(eventId);
    if (event == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(event);
}

@GetMapping("/dashboard/student/event-activities/view-all")
public String showAllEventActivities(Model model) {
    List<EventActivity> eventActivities = eventActivitiesService.getAllEventActivities();
    model.addAttribute("eventActivities", eventActivities);
    return "event-activities/view-all";
}

@GetMapping("/dashboard/officer/event-activities/officer-event-report")
public String showAllEventsPage(Model model) {
    // Fetch all events and add them to the model
    List<EventActivity> events = eventActivitiesService.getAllEventActivities();
    model.addAttribute(ATTR_EVENTS, events);
    return "event-activities/officer-event-report";
}


    @GetMapping("/event-activities/details/{eventId}")
    public String getEventDetails(@PathVariable Long eventId, Model model) {
        EventActivity event = eventActivitiesService.getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        model.addAttribute("event", event);
        return "event-activities/event-details";
    }

    @PostMapping("/event-activities/update-event")
    public ResponseEntity<Map<String, String>> updateEvent(@ModelAttribute EventActivity event) {
        if (event.getId() == null) {
            return ResponseEntity.badRequest().body(Map.of(KEY_SUCCESS, "false", KEY_MESSAGE, "Event ID is missing."));
        }

        try {
            eventActivitiesService.updateEvent(event);
            return ResponseEntity.ok(Map.of(KEY_SUCCESS, "true", KEY_MESSAGE, "Event updated successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(KEY_SUCCESS, "false", KEY_MESSAGE, "Failed to update event."));
        }
    }

    @DeleteMapping("/delete-event/{id}")
    public ResponseEntity<List<EventActivity>> deleteEvent(@PathVariable Long id) {
        eventActivitiesService.deleteEventActivity(id);
        List<EventActivity> updatedEvents = eventActivitiesService.getAllEventActivities();
        return ResponseEntity.ok(updatedEvents);
    }

    @GetMapping("/dashboard/events")
    public String showStudentEvents(Model model) {
        model.addAttribute(ATTR_EVENTS, eventActivitiesService.getAllEventActivities());
        return "dashboard/events";
    }

    @GetMapping("/events-dashboard") // Change the path to avoid conflict
public String showDashboard(Model model) {
    List<EventActivity> events = eventActivitiesService.getAllEventActivities();
    model.addAttribute("eventsList", events != null ? events : new ArrayList<>());
    return "dashboard/dashboard-student";
}

}
