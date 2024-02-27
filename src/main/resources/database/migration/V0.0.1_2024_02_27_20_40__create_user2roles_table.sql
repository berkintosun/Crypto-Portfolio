CREATE TABLE user2roles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    CONSTRAINT fk_user
            FOREIGN KEY (user_id)
                REFERENCES users(id),
    CONSTRAINT fk_role
            FOREIGN KEY (role_id)
                REFERENCES roles(id)
)