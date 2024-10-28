INSERT INTO role(authority) VALUES ('ADMIN');
INSERT INTO role(authority) VALUES ('USER');
INSERT INTO role(authority) VALUES ('VIP');

INSERT INTO user(email, password, name, nickname, phone, enabled, created_at) VALUES ('test@naver.com', '$2a$10$OPGzvosDCDo4K7xPqieluejJ0osb.UheqlhRPHf.1qCm9gIYpI4cG', 'name', 'nickname', '01051969963', 1, now());
insert into user_role(user_id, role_id) values (1, 2);

INSERT INTO counselor(nickname,service_number, enabled, created_at) VALUES ('counselorNickname', '111', 1, now());
