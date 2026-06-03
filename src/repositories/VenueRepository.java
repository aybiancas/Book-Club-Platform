package repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.*;
import model.Venue;
import model.VenueType;

public class VenueRepository {

    private static VenueRepository instance;

    private VenueRepository() {}

    public static VenueRepository getInstance() {
        if (instance == null) {
            instance = new VenueRepository();
        }
        return instance;
    }

    private Connection connection() {
        return ConnectionProvider.getInstance().getConnection();
    }

    private Venue mapRow(ResultSet rs) throws SQLException {
        return new Venue(rs.getInt("venue_id"), rs.getString("name"),
                rs.getString("address"), rs.getInt("capacity"),
                VenueType.valueOf(rs.getString("venue_type")));
    }

    public void addVenue(Venue venue) {
        String sql = "insert into venues (name, address, capacity, venue_type) values (?, ?, ?, ?::venue_type_enum)";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, venue.getName());
            preparedStatement.setString(2, venue.getAddress());
            preparedStatement.setInt(3, venue.getCapacity());
            preparedStatement.setString(4, venue.getVenueType().name());
            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) venue.restoreVenueId(keys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateVenue(Venue venue) {
        String sql = "update venues set name = ?, address = ?, capacity = ?, venue_type = ?::venue_type_enum where venue_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setString(1, venue.getName());
            preparedStatement.setString(2, venue.getAddress());
            preparedStatement.setInt(3, venue.getCapacity());
            preparedStatement.setString(4, venue.getVenueType().name());
            preparedStatement.setInt(5, venue.getVenueId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVenue(int venueId) {
        String sql = "delete from venues where venue_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, venueId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Venue findById(int venueId) {
        String sql = "select * from venues where venue_id = ?";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql)) {

            preparedStatement.setInt(1, venueId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Venue> findAll() {
        List<Venue> list = new ArrayList<>();
        String sql = "select * from venues order by name";
        try (PreparedStatement preparedStatement = connection().prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
