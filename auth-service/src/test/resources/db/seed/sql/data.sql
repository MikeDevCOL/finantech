INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');

INSERT INTO users (username, password, email, role_id) VALUES 
('admin', '$2a$12$ZnENoVLOapVLvxlSMJiJOu3R7IdaT0BJcc7CM89PsF5/EgkGXzJs6', 'admin@example.com', 1),
('user', '$2a$12$M.y0Yx4B4MeESo9pfGcoNeXH8hBr7eHmaFIbFoO0IZG7JTZjZn/6u', 'user@example.com', 2); 
-- Password is '12345678901112' hashed using BCrypt