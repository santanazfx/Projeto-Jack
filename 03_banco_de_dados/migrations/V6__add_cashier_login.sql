ALTER TABLE users ADD COLUMN username VARCHAR(80);
UPDATE users SET username = email WHERE username IS NULL;
ALTER TABLE users ALTER COLUMN username SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT uk_users_username UNIQUE (username);

CREATE INDEX idx_users_username_active ON users(username, active);

INSERT INTO users (name, username, email, password_hash, active)
SELECT 'Jack Evandro', 'jack evandro', 'caixa@jackburger.local', '$2y$12$fm5bQv0YkwSofLUQFprjbuU.1Erw2meNF5Heb2.KcEs7BpuMAk.fy', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'jack evandro');

INSERT INTO user_roles (user_id, role_id)
SELECT user_account.id, role_account.id
FROM users user_account
JOIN roles role_account ON role_account.code = 'CAIXA'
WHERE user_account.username = 'jack evandro'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles existing_user_role
    WHERE existing_user_role.user_id = user_account.id AND existing_user_role.role_id = role_account.id
  );
