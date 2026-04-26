package classes;

public class Venue {
    private String venueId;
    private String name;
    private String address;
    private int capacity;
    private int venueType;

    public Venue(String venueId, String name, String address, int capacity, int venueType) {
        this.venueId = venueId;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.venueType = venueType;
    }

    public String getVenueId() {
        return venueId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getVenueType() {
        return venueType;
    }

    public boolean canAccommodate(int numberOfPeople) {
        return capacity == 0 || numberOfPeople <= capacity;
    }
}
