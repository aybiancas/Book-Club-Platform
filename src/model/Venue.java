package model;

import java.util.Objects;

public class Venue {
    private int venueId;
    private String name;
    private String address;
    private int capacity;
    private VenueType venueType;

    public Venue(int venueId, String name, String address, int capacity, VenueType venueType) {
        this.venueId = venueId;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.venueType = venueType;
    }

    public Venue(String name, String address, int capacity, VenueType venueType) {
        this.venueId = 0;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.venueType = venueType;
    }

    public int getVenueId() {
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

    public VenueType getVenueType() {
        return venueType;
    }

    public void restoreVenueId(int id) {
        this.venueId = id;
    }

    public boolean canAccommodate(int numberOfPeople) {
        return capacity == 0 || numberOfPeople <= capacity;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setVenueType(VenueType venueType) {
        this.venueType = venueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venue)) {
            return false;
        }
        return venueId == ((Venue) o).venueId;
    }
    @Override
    public int hashCode() {
        return Objects.hash(venueId);
    }

    @Override
    public String toString() {
        return name + " (" + venueType + ") — " + address;
    }
}
