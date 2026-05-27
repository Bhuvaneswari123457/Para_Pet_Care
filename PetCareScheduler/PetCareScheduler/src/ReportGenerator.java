import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ReportGenerator {

    /**
     * Report 1: Pets with upcoming appointments within the next 7 days.
     */
    public static void upcomingAppointmentsReport(Map<String, Pet> pets) {
        System.out.println("\n========================================");
        System.out.println("  REPORT: Upcoming Appointments (Next 7 Days)");
        System.out.println("========================================");

        LocalDateTime now  = LocalDateTime.now();
        LocalDateTime weekLater = now.plusDays(7);
        boolean found = false;

        for (Pet pet : pets.values()) {
            for (Appointment appt : pet.getAppointments()) {
                LocalDateTime dt = appt.getDateTime();
                if (dt.isAfter(now) && dt.isBefore(weekLater)) {
                    System.out.printf("  Pet: %-15s (ID: %s)%n", pet.getName(), pet.getPetId());
                    System.out.println("    " + appt);
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("  No upcoming appointments in the next 7 days.");
        }
        System.out.println("========================================\n");
    }

    /**
     * Report 2: Pets overdue for a vet visit (no vet visit in the last 6 months).
     */
    public static void overdueVetVisitReport(Map<String, Pet> pets) {
        System.out.println("\n========================================");
        System.out.println("  REPORT: Pets Overdue for Vet Visit (6+ Months)");
        System.out.println("========================================");

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        boolean found = false;

        for (Pet pet : pets.values()) {
            LocalDateTime lastVet = null;

            for (Appointment appt : pet.getAppointments()) {
                boolean isVet = appt.getAppointmentType().equalsIgnoreCase("vet visit")
                             || appt.getAppointmentType().toLowerCase().contains("vet");
                if (isVet && appt.isPast()) {
                    if (lastVet == null || appt.getDateTime().isAfter(lastVet)) {
                        lastVet = appt.getDateTime();
                    }
                }
            }

            boolean overdue = (lastVet == null) || lastVet.isBefore(sixMonthsAgo);
            if (overdue) {
                String lastVisitStr = (lastVet == null) ? "Never" : lastVet.format(Appointment.FORMATTER);
                System.out.printf("  %-15s (ID: %-8s) | Owner: %-15s | Last Vet Visit: %s%n",
                    pet.getName(), pet.getPetId(), pet.getOwnerName(), lastVisitStr);
                found = true;
            }
        }
        if (!found) {
            System.out.println("  All pets are up to date with vet visits.");
        }
        System.out.println("========================================\n");
    }
}
