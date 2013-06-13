  DROP DATABASE IF EXISTS test;
    CREATE DATABASE test;
    USE test;
   CREATE TABLE a1 (
    knr INTEGER,
    kname VARCHAR(255),
    PRIMARY KEY (knr)
   ) ENGINE = INNODB;
    INSERT INTO a1 VALUES (1, 'Max Mustermann');
    INSERT INTO a1 VALUES (2, 'Heidi Musterfrau');
