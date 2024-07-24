CREATE TABLE IF NOT EXISTS transaction (
    id SERIAL PRIMARY KEY,
    "type" INT,
    "date" DATE,
    "value" DECIMAL,
     cpf BIGINT,
    card VARCHAR(255),
    "hour" TIME,
    owner VARCHAR(255),
    "name" VARCHAR(255)
);