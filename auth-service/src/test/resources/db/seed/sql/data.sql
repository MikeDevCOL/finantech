INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');

INSERT INTO users (username, password, email, role_id) VALUES 
('admin', '$2a$12$WpMldDYs44TC2jYGP4IybeZdxwNjsgGdSfw3oNa5r/hgWkVfn6WHa', 'admin@example.com', 1),
('user', '$2a$12$WpMldDYs44TC2jYGP4IybeZdxwNjsgGdSfw3oNa5r/hgWkVfn6WHa', 'user@example.com', 2); 
-- Password is '123456' hashed using BCrypt