package server.db;

import common.models.Car;
import common.models.Coordinates;
import common.models.HumanBeing;
import common.models.WeaponType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresCollectionRepository implements CollectionRepository<HumanBeing> {
    private final Connection connection;

    public PostgresCollectionRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long generateNextId() throws DatabaseException {
        String sql = "SELECT nextval('human_being_id_seq')";

        try (
                PreparedStatement statement = this.connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

            throw new DatabaseException("Не удалось получить следующий id из sequence");
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при генерации id", e);
        }
    }

    @Override
    public void add(HumanBeing humanBeing) throws DatabaseException {
        String sql = """
                INSERT INTO human_beings (
                    id,
                    name,
                    coord_x,
                    coord_y,
                    creation_date,
                    real_hero,
                    has_toothpick,
                    impact_speed,
                    soundtrack_name,
                    minutes_of_waiting,
                    weapon_type,
                    car_cool,
                    user_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, humanBeing.getId());
            statement.setString(2, humanBeing.getName());
            statement.setInt(3, humanBeing.getCoordinates().getX());
            statement.setLong(4, humanBeing.getCoordinates().getY());
            statement.setTimestamp(5, Timestamp.valueOf(humanBeing.getCreationDate()));
            statement.setBoolean(6, humanBeing.isRealHero());
            statement.setBoolean(7, humanBeing.isHasToothpick());
            statement.setDouble(8, humanBeing.getImpactSpeed());
            statement.setString(9, humanBeing.getSoundtrackName());
            statement.setInt(10, humanBeing.getMinutesOfWaiting());
            statement.setString(11, humanBeing.getWeaponType() == null ? null : humanBeing.getWeaponType().name());
            statement.setObject(12, humanBeing.getCar() == null ? null : humanBeing.getCar().isCool(), Types.BOOLEAN);
            statement.setLong(13, humanBeing.getUserId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при добавлении объекта в БД", e);
        }
    }

    @Override
    public void update(HumanBeing humanBeing) throws DatabaseException {
        String sql = """
                UPDATE human_beings
                SET name = ?,
                    coord_x = ?,
                    coord_y = ?,
                    creation_date = ?,
                    real_hero = ?,
                    has_toothpick = ?,
                    impact_speed = ?,
                    soundtrack_name = ?,
                    minutes_of_waiting = ?,
                    weapon_type = ?,
                    car_cool = ?
                WHERE id = ? AND user_id = ?
                """;

        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, humanBeing.getName());
            statement.setInt(2, humanBeing.getCoordinates().getX());
            statement.setLong(3, humanBeing.getCoordinates().getY());
            statement.setTimestamp(4, Timestamp.valueOf(humanBeing.getCreationDate()));
            statement.setBoolean(5, humanBeing.isRealHero());
            statement.setBoolean(6, humanBeing.isHasToothpick());
            statement.setDouble(7, humanBeing.getImpactSpeed());
            statement.setString(8, humanBeing.getSoundtrackName());
            statement.setInt(9, humanBeing.getMinutesOfWaiting());
            statement.setString(10, humanBeing.getWeaponType() == null ? null : humanBeing.getWeaponType().name());
            statement.setObject(11, humanBeing.getCar() == null ? null : humanBeing.getCar().isCool(), Types.BOOLEAN);
            statement.setLong(12, humanBeing.getId());
            statement.setLong(13, humanBeing.getUserId());

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new DatabaseException("Объект не найден или не принадлежит пользователю");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при обновлении объекта в БД", e);
        }
    }

    @Override
    public void remove(long id) throws DatabaseException {
        String sql = "DELETE FROM human_beings WHERE id = ?";

        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при удалении объекта из БД", e);
        }
    }

    @Override
    public void clear(Long userId) throws DatabaseException {
        String sql = "DELETE FROM human_beings WHERE user_id = ?";

        try (
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при очистке объектов пользователя", e);
        }
    }

    @Override
    public List<HumanBeing> loadAll() throws DatabaseException {
        String sql = """
                SELECT id,
                       name,
                       coord_x,
                       coord_y,
                       creation_date,
                       real_hero,
                       has_toothpick,
                       impact_speed,
                       soundtrack_name,
                       minutes_of_waiting,
                       weapon_type,
                       car_cool,
                       user_id
                FROM human_beings
                """;

        List<HumanBeing> result = new ArrayList<>();

        try (
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                result.add(mapRow(resultSet));
            }

            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при загрузке коллекции из БД", e);
        }
    }

    private HumanBeing mapRow(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String creationDate = resultSet.getTimestamp("creation_date").toLocalDateTime().toString();

        String name = resultSet.getString("name");

        int coordX = resultSet.getInt("coord_x");
        long coordY = resultSet.getLong("coord_y");
        Coordinates coordinates = new Coordinates(coordX, coordY);

        boolean realHero = resultSet.getBoolean("real_hero");
        boolean hasToothpick = resultSet.getBoolean("has_toothpick");

        double impactSpeed = resultSet.getDouble("impact_speed");
        String soundtrackName = resultSet.getString("soundtrack_name");
        int minutesOfWaiting = resultSet.getInt("minutes_of_waiting");

        String weaponTypeValue = resultSet.getString("weapon_type");
        WeaponType weaponType = weaponTypeValue == null ? null : WeaponType.valueOf(weaponTypeValue);

        Boolean carCool = getNullableBoolean(resultSet, "car_cool");
        Car car = carCool == null ? null : new Car(carCool);

        Long userId = resultSet.getLong("user_id");

        HumanBeing humanBeing = new HumanBeing(
                id,
                creationDate,
                name,
                coordinates,
                realHero,
                hasToothpick,
                impactSpeed,
                soundtrackName,
                minutesOfWaiting,
                weaponType,
                car
        );

        humanBeing.setUserId(userId);

        return humanBeing;
    }

    private Boolean getNullableBoolean(ResultSet resultSet, String columnName) throws SQLException {
        boolean value = resultSet.getBoolean(columnName);
        return resultSet.wasNull() ? null : value;
    }
}
