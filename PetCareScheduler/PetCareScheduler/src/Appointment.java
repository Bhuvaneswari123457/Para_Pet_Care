import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String appointmentType;
    private LocalDateTime dateTime;
    private String notes;

    public Appointment(String appointmentType, LocalDateTime dateTime, String notes) {
        this.appointmentType = appointmentType;
        this.dateTime = dateTime;
        this.notes = (notes == null || notes.isBlank()) ? "N/A" : notes;
    }

    // Getters
    public String getAppointmentType() { return appointmentType; }
    public LocalDateTime getDateTime()  { return dateTime; }
    public String getNotes()            { return notes; }

    // Setters
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }
    public void setDateTime(LocalDateTime dateTime)        { this.dateTime = dateTime; }
    public void setNotes(String notes)                     { this.notes = notes; }

    public boolean isUpcoming() {
        return dateTime.isAfter(LocalDateTime.now());
    }

    public boolean isPast() {
        return dateTime.isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return String.format(
            "Type: %-15s | Date/Time: %-16s | Notes: %s",
            appointmentType, dateTime.format(FORMATTER), notes
        );
    }
}
