import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pet {
    private String petId;
    private String name;
    private String speciesBreed;
    private int age;
    private String ownerName;
    private String contactInfo;
    private LocalDate registrationDate;
    private List<Appointment> appointments;

    public Pet(String petId, String name, String speciesBreed, int age,
               String ownerName, String contactInfo, LocalDate registrationDate) {
        this.petId = petId;
        this.name = name;
        this.speciesBreed = speciesBreed;
        this.age = age;
        this.ownerName = ownerName;
        this.contactInfo = contactInfo;
        this.registrationDate = registrationDate;
        this.appointments = new ArrayList<>();
    }

    // Getters
    public String getPetId()           { return petId; }
    public String getName()            { return name; }
    public String getSpeciesBreed()    { return speciesBreed; }
    public int getAge()                { return age; }
    public String getOwnerName()       { return ownerName; }
    public String getContactInfo()     { return contactInfo; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public List<Appointment> getAppointments() { return appointments; }

    // Setters
    public void setName(String name)               { this.name = name; }
    public void setSpeciesBreed(String speciesBreed) { this.speciesBreed = speciesBreed; }
    public void setAge(int age)                    { this.age = age; }
    public void setOwnerName(String ownerName)     { this.ownerName = ownerName; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    @Override
    public String toString() {
        return String.format(
            "Pet ID: %s | Name: %s | Species/Breed: %s | Age: %d | Owner: %s | Contact: %s | Registered: %s",
            petId, name, speciesBreed, age, ownerName, contactInfo, registrationDate
        );
    }
}
