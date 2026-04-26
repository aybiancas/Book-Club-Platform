package enums;

public enum VenueType {
    Physical(1),
    Online(2);

    private final int value;

    private VenueType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
