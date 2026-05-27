import  java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class PetCareScheduler {

    private static final Map<String, Pet> petRegistry = new LinkedHashMap<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadData(); // Load persisted data on startup
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║   Welcome to Paws & Whiskers Care     ║");
        System.out.println("║     Pet Care Scheduler v1.0           ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readIntSafe("Enter your choice: ");
            switch (choice) {
                case 1  -> registerPet();
                case 2  -> scheduleAppointment();
                case 3  -> displayRecords();
                case 4  -> saveData();
                case 5  -> generateReports();
                case 6  -> { saveData(); running = false; }
                default -> System.out.println("[!] Invalid choice. Please enter 1–6.");
            }
        }
        System.out.println("\nThank you for using Paws & Whiskers. Goodbye!");
        scanner.close();
    }

    // ── PRIVATE: Load data at startup ─────────────────────────────────────
    private static void loadData() {
        System.out.println("\n[INFO] Loading saved data...");
        Map<String, Pet> loaded = FileManager.loadPets();
        petRegistry.putAll(loaded);
        FileManager.loadAppointments(petRegistry);
    }

    // ── Menu ───────────────────────────────────────────────────────────────
    private static void printMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│            MAIN MENU                │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Register a Pet                  │");
        System.out.println("│  2. Schedule an Appointment         │");
        System.out.println("│  3. Display Records                 │");
        System.out.println("│  4. Save Data                       │");
        System.out.println("│  5. Generate Reports                │");
        System.out.println("│  6. Save & Exit                     │");
        System.out.println("└─────────────────────────────────────┘");
    }

    // ── 1. Register a Pet ─────────────────────────────────────────────────
    private static void registerPet() {
        System.out.println("\n--- Register New Pet ---");

        // Unique Pet ID
        String petId;
        while (true) {
            petId = readStringSafe("Enter Pet ID (e.g., P001): ").toUpperCase();
            if (petRegistry.containsKey(petId)) {
                System.out.println("[!] Pet ID '" + petId + "' already exists. Please use a unique ID.");
            } else {
                break;
            }
        }

        String name        = readStringSafe("Enter Pet Name: ");
        String speciesBreed = readStringSafe("Enter Species/Breed (e.g., Dog - Labrador): ");

        // Age — with error handling
        int age = -1;
        while (age < 0) {
            try {
                age = readIntSafe("Enter Age (years): ");
                if (age < 0) throw new NumberFormatException("Age cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid age. Please enter a non-negative whole number.");
                age = -1;
            }
        }

        String ownerName   = readStringSafe("Enter Owner Name: ");
        String contactInfo = readStringSafe("Enter Contact Info (phone/email): ");

        // Registration date — defaults to today; optionally override
        LocalDate regDate = LocalDate.now();
        System.out.print("Registration Date (YYYY-MM-DD) [press Enter for today " + regDate + "]: ");
        String dateInput = scanner.nextLine().trim();
        if (!dateInput.isEmpty()) {
            while (true) {
                try {
                    regDate = LocalDate.parse(dateInput);
                    break;
                } catch (DateTimeParseException e) {
                    System.out.print("[!] Invalid date format. Enter YYYY-MM-DD or press Enter for today: ");
                    dateInput = scanner.nextLine().trim();
                    if (dateInput.isEmpty()) break;
                }
            }
        }

        Pet pet = new Pet(petId, name, speciesBreed, age, ownerName, contactInfo, regDate);
        petRegistry.put(petId, pet);

        System.out.println("\n[✓] Pet registered successfully!");
        System.out.println("    " + pet);
    }

    // ── 2. Schedule an Appointment ────────────────────────────────────────
    private static void scheduleAppointment() {
        System.out.println("\n--- Schedule Appointment ---");
        if (petRegistry.isEmpty()) {
            System.out.println("[!] No pets registered yet. Please register a pet first.");
            return;
        }

        String petId = readStringSafe("Enter Pet ID: ").toUpperCase();
        Pet pet = petRegistry.get(petId);
        if (pet == null) {
            System.out.println("[!] Pet ID '" + petId + "' not found.");
            return;
        }

        System.out.println("Appointment Types: Vet Visit | Vaccination | Grooming | Dental | Other");
        String apptType = readStringSafe("Enter Appointment Type: ");

        // Date and time with error handling
        LocalDateTime apptDateTime = null;
        while (apptDateTime == null) {
            String dtInput = readStringSafe("Enter Date & Time (YYYY-MM-DD HH:mm, 24hr): ");
            try {
                apptDateTime = LocalDateTime.parse(dtInput, Appointment.FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("[!] Invalid format. Use YYYY-MM-DD HH:mm (e.g., 2025-08-20 09:30).");
            }
        }

        System.out.print("Enter Notes (optional, press Enter to skip): ");
        String notes = scanner.nextLine().trim();

        Appointment appt = new Appointment(apptType, apptDateTime, notes);
        pet.addAppointment(appt);

        System.out.println("\n[✓] Appointment scheduled for " + pet.getName() + "!");
        System.out.println("    " + appt);
    }

    // ── 3. Display Records ────────────────────────────────────────────────
    private static void displayRecords() {
        System.out.println("\n--- Display Records ---");
        System.out.println("  1. All registered pets");
        System.out.println("  2. All appointments for a specific pet");
        System.out.println("  3. Upcoming appointments for all pets");
        System.out.println("  4. Past appointment history for each pet");
        int sub = readIntSafe("Select option: ");

        switch (sub) {
            case 1 -> displayAllPets();
            case 2 -> displayPetAppointments();
            case 3 -> displayUpcomingAll();
            case 4 -> displayPastHistory();
            default -> System.out.println("[!] Invalid option.");
        }
    }

    private static void displayAllPets() {
        System.out.println("\n======== Registered Pets (" + petRegistry.size() + ") ========");
        if (petRegistry.isEmpty()) { System.out.println("  No pets registered."); return; }
        petRegistry.values().forEach(p -> System.out.println("  " + p));
        System.out.println("=".repeat(44));
    }

    private static void displayPetAppointments() {
        String petId = readStringSafe("Enter Pet ID: ").toUpperCase();
        Pet pet = petRegistry.get(petId);
        if (pet == null) { System.out.println("[!] Pet not found."); return; }

        System.out.println("\n=== Appointments for " + pet.getName() + " (ID: " + petId + ") ===");
        List<Appointment> list = pet.getAppointments();
        if (list.isEmpty()) { System.out.println("  No appointments scheduled."); return; }
        list.forEach(a -> System.out.println("  " + a));
        System.out.println("=".repeat(44));
    }

    private static void displayUpcomingAll() {
        System.out.println("\n======== Upcoming Appointments ========");
        boolean any = false;
        for (Pet pet : petRegistry.values()) {
            for (Appointment a : pet.getAppointments()) {
                if (a.isUpcoming()) {
                    System.out.printf("  [%s] %-12s → %s%n", pet.getPetId(), pet.getName(), a);
                    any = true;
                }
            }
        }
        if (!any) System.out.println("  No upcoming appointments found.");
        System.out.println("=".repeat(44));
    }

    private static void displayPastHistory() {
        System.out.println("\n======== Past Appointment History ========");
        boolean any = false;
        for (Pet pet : petRegistry.values()) {
            List<Appointment> past = pet.getAppointments().stream()
                .filter(Appointment::isPast).toList();
            if (!past.isEmpty()) {
                System.out.println("  " + pet.getName() + " (ID: " + pet.getPetId() + "):");
                past.forEach(a -> System.out.println("    " + a));
                any = true;
            }
        }
        if (!any) System.out.println("  No past appointments found.");
        System.out.println("=".repeat(44));
    }

    // ── 4. Save Data ──────────────────────────────────────────────────────
    private static void saveData() {
        FileManager.savePets(petRegistry);
        FileManager.saveAppointments(petRegistry);
    }

    // ── 5. Generate Reports ───────────────────────────────────────────────
    private static void generateReports() {
        System.out.println("\n--- Generate Reports ---");
        System.out.println("  1. Pets with upcoming appointments (next 7 days)");
        System.out.println("  2. Pets overdue for a vet visit (6+ months)");
        int sub = readIntSafe("Select report: ");
        switch (sub) {
            case 1 -> ReportGenerator.upcomingAppointmentsReport(petRegistry);
            case 2 -> ReportGenerator.overdueVetVisitReport(petRegistry);
            default -> System.out.println("[!] Invalid option.");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private static String readStringSafe(String prompt) {
        String input = "";
        while (input.isBlank()) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isBlank()) System.out.println("[!] Input cannot be empty.");
        }
        return input;
    }

    private static int readIntSafe(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[!] Please enter a valid number.");
            }
        }
    }
}
