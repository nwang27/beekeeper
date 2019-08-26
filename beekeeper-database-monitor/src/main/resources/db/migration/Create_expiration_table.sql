
CREATE TABLE expiration (
  id BIGINT(20) AUTO_INCREMENT,
  path VARCHAR(10000) NOT NULL,
  database_name VARCHAR(512),
  table_name VARCHAR(512),
  path_status VARCHAR(50) NOT NULL,
  cleanup_delay VARCHAR(50) NOT NULL,
  creation_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  cleanup_timestamp TIMESTAMP NOT NULL,
  cleanup_attempts INT NOT NULL DEFAULT 0,
  client_id VARCHAR(512),
  expiration_days INT NOT NULL,
  PRIMARY KEY (id)
);
