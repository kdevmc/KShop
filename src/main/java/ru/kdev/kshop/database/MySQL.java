package ru.kdev.kshop.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQL {
    private Connection connection;

    private void handleCause(SQLException e) {
        e.printStackTrace();
    }

    public void connect(ConfigurationSection section) {
        connect(
                section.getString("host"),
                section.getInt("port"),
                section.getString("database"),
                section.getString("user"),
                section.getString("password")
        );
    }

    public void connect(String host, int port, String database, String user, String password) {
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(host);
            dataSource.setPort(port);
            dataSource.setDatabaseName(database);
            dataSource.setUser(user);
            dataSource.setPassword(password);
            dataSource.setServerTimezone("UTC");

            connection = dataSource.getConnection();
        } catch (SQLException e) {
            handleCause(e);
        }
    }

    public void addItem(Player player, String pattern, int quantity, int data, String nbt) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO items (nickname, pattern, quantity, data, nbt) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, player.getName());
        statement.setString(2, pattern);
        statement.setInt(3, quantity);
        statement.setInt(4, data);
        statement.setString(5, nbt);
        statement.executeUpdate();
    }

    public ResultSet getItem(Player player, int index) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE nickname = ?");
            statement.setString(1, player.getName());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            resultSet.previous();
            List<Integer> rows = new ArrayList<Integer>();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                rows.add(id);
            }
            PreparedStatement getStatement = connection.prepareStatement("SELECT * FROM items WHERE id = ?");
            getStatement.setInt(1, rows.get(index));
            return getStatement.executeQuery();
        } catch (SQLException e) {
            handleCause(e);
            return null;
        }
    }

    public void removeItem(Player player, int index) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE nickname = ?");
        statement.setString(1, player.getName());
        ResultSet resultSet = statement.executeQuery();
        if(!resultSet.next()) {
            return;
        }
        resultSet.previous();
        List<Integer> rows = new ArrayList<Integer>();
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            rows.add(id);
        }
        PreparedStatement removeStatement = connection.prepareStatement("DELETE FROM items WHERE id = ?");
        removeStatement.setInt(1, rows.get(index));
        removeStatement.executeUpdate();
    }

    public void removeItems(Player player) throws SQLException {
        PreparedStatement removeStatement = connection.prepareStatement("DELETE FROM items WHERE nickname = ?");
        removeStatement.setString(1, player.getName());
        removeStatement.executeUpdate();
    }

    public ResultSet getGroups(Player player) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM groups WHERE nickname = ?");
        statement.setString(1, player.getName());
        return statement.executeQuery();
    }

    public ResultSet getItems(Player player) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE nickname = ?");
        statement.setString(1, player.getName());
        return statement.executeQuery();
    }

    public void removeGroup(Player player, String gName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM groups WHERE nickname = ? AND groupName = ?");
        statement.setString(1, player.getName());
        statement.setString(2, gName);
        statement.executeUpdate();
    }
}
