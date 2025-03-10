--liquibase formatted sql
--changeset ldcnetto:202503091845
--comment: boards table create

CREATE TABLE BOARDS(
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL
)