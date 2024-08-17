-- liquibase formatted sql

-- changeset dporoshin:1

CREATE INDEX faculty_name_and_color_index ON faculty (name, color);