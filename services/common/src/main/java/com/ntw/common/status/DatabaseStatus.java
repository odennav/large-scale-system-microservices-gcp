package com.ntw.common.status;

import com.google.gson.Gson;

public class DatabaseStatus {
    private String database;
    private String connection;
    private String databaseTime;

    public DatabaseStatus() {
        this.database = "Uninitialized";
        this.connection = "Not Configured";
        this.databaseTime = "Not Attempted";
    }

    public DatabaseStatus(String database) {
        this();
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getDatabaseTime() {
        return databaseTime;
    }

    public void setDatabaseTime(String databaseTime) {
        this.databaseTime = databaseTime;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return "{" +
                "\"database\":" + (database == null ? "null" : "\"" + database + "\"") + ", " +
                "\"connection\":" + (connection == null ? "null" : "\"" + connection + "\"") + ", " +
                "\"databaseTime\":" + (databaseTime == null ? "null" : "\"" + databaseTime + "\"") +
                "}";
    }
}
