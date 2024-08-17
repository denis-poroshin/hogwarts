-- liquibase formatted sql

-- changeset dporoshin:1

CREATE INDEX student_name_index ON student (name);