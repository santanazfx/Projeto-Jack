SHOW TABLES;

SELECT COUNT(*) AS total_produtos
FROM products;

SELECT id, name, price, available
FROM products
ORDER BY id;

SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
