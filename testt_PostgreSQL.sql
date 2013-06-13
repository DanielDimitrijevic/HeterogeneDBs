

\c template1
DROP DATABASE test;
CREATE DATABASE test;
\c test

-- DROP TABLE IF EXISTS kellner CASCADE;
CREATE TABLE a1 (
             knr         INTEGER,
             kname        VARCHAR(255),
             PRIMARY KEY (knr)
             );




