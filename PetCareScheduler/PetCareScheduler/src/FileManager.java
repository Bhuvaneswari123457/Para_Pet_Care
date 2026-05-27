import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class FileManager {
    private static final String DATA_DIR  = "data/";
    private static final String PETS_FILE = DATA_DIR + "pets.txt";
    private static final String APPT_FILE = DATA_DIR + "appointments.txt";

    // Ensure data directory exists
    static {
        new File(DATA_DIR).mkdirs();
    }

    /** Save all pets (without appointments) to pets.txt */
    public static void savePets(Map<String, Pet> pets) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PETS_FILE))) {
            for (Pet pet : pets.values()) {
                writer.write(String.join("|",
                    pet.getPetId(),
                    pet.getName(),
                    pet.getSpeciesBreed(),
                    String.valueOf(pet.getAge()),
                    pet.getOwnerName(),
                    pet.getContactInfo(),
                    pet.getRegistrationDate().toString()
                ));
                writer.newLine();
            }
            System.out.println("[INFO] Pet data saved successfully.");
        } catch (IOException e) {
            System.err.println("[ERROR] Could not save pet data: " + e.getMessage());
        }
    }

    /** Save all appointments to appointments.txt */
    public static void saveAppointments(Map<String, Pet> pets) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPT_FILE))) {
            for (Pet pet : pets.values()) {
                for (Appointment appt : pet.getAppointments()) {
                    writer.write(String.join("|",
                        pet.getPetId(),
                        appt.getAppointmentType(),
                        appt.getDateTime().format(Appointment.FORMATTER),
                        appt.getNotes()
                    ));
                    writer.newLine();
                }
            }
            System.out.println("[INFO] Appointment data saved successfully.");
        } catch (IOException e) {
            System.err.println("[ERROR] Could not save appointment data: " + e.getMessage());
        }
    }

    /** Load pets from file; returns a LinkedHashMap to preserve insertion order */
    public static Map<String, Pet> loadPets() {
        Map<String, Pet> pets = new LinkedHashMap<>();
        File file = new File(PETS_FILE);
        if (!file.exists()) return pets;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|", 7);
                if (parts.length < 7) continue;
                try {
                    Pet pet = new Pet(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        Integer.parseInt(parts[3].trim()),
                        parts[4].trim(),
                        parts[5].trim(),
                        LocalDate.parse(parts[6].trim())
                    );
                    pets.put(pet.getPetId(), pet);
                } catch (Exception e) {
                    System.err.println("[WARN] Skipping malformed pet record: " + line);
                }
            }
            System.out.println("[INFO] Loaded " + pets.size() + " pet(s) from file.");
        } catch (IOException e) {
            System.err.println("[ERROR] Could not load pet data: " + e.getMessage());
        }
        return pets;
    }

    /** Load appointments and attach them to their pets */
    public static void loadAppointments(Map<String, Pet> pets) {
        File file = new File(APPT_FILE);
        if (!file.exists()) return;

        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|", 4);
                if (parts.length < 4) continue;
                try {
                    String petId = parts[0].trim();
                    Pet pet = pets.get(petId);
                    if (pet == null) continue;
                    Appointment appt = new Appointment(
                        parts[1].trim(),
                        LocalDateTime.parse(parts[2].trim(), Appointment.FORMATTER),
                        parts[3].trim()
                    );
                    pet.addAppointment(appt);
                    count++;
                } catch (Exception e) {
                    System.err.println("[WARN] Skipping malformed appointment record: " + line);
                }
            }
            System.out.println("[INFO] Loaded " + count + " appointment(s) from file.");
        } catch (IOException e) {
            System.err.println("[ERROR] Could not load appointment data: " + e.getMessage());
        }
    }
}
