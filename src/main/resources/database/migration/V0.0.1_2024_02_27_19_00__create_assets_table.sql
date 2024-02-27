CREATE TABLE assets (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    amount NUMERIC(20,10),
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
)