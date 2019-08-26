package com.expediagroup.beekeeper.core.model.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

import com.expediagroup.beekeeper.core.model.DurationConverter;
import com.expediagroup.beekeeper.core.model.ExpirationDate;
import com.expediagroup.beekeeper.core.model.PathStatus;

@Entity
@Table(name = "expiration")
public class EntityExpirationDate implements ExpirationDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path", nullable = false, unique = true)
    private String path;

    @Column(name = "database_name")
    private String databaseName;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "path_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PathStatus pathStatus;

    @Column(name = "creation_timestamp", nullable = false, updatable = false)
    private LocalDateTime creationTimestamp;

    @Column(name = "modified_timestamp")
    @UpdateTimestamp
    private LocalDateTime modifiedTimestamp;

    @Column(name = "cleanup_timestamp", nullable = false)
    private LocalDateTime cleanupTimestamp;

    @Column(name = "cleanup_delay", nullable = false)
    @Convert(converter = DurationConverter.class)
    private Duration cleanupDelay;

    @Column(name = "cleanup_attempts", nullable = false)
    private int cleanupAttempts;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "expiration_days", nullable = false, unique = true)
    private int expirationDays;



    public EntityExpirationDate(final Long id, final int expirationDays, final String databaseName, final String tableName) {
        this.id = id;
        this.expirationDays = expirationDays;
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public int getExpirationDays() {
        return expirationDays;
    }

    @Override
    public void setExpirationDays(final int expirationDate) {
        this.expirationDays = expirationDays;
    }

    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }

    @Override
    public void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
}
