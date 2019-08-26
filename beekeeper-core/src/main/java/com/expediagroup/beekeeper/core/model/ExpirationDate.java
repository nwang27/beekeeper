package com.expediagroup.beekeeper.core.model;

import java.util.Date;

public interface ExpirationDate {

    Long getId();

    int getExpirationDays();

    void setExpirationDays(int expirationDays);

    String getDatabaseName();

    void setDatabaseName(String databaseName);

    String getTableName();

    void setTableName(String tableName);
}
