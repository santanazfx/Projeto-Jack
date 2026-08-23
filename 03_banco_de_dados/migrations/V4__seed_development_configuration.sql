INSERT INTO roles (id, code, name, description) VALUES
 (1, 'ADMIN', 'Administrador', 'Acesso administrativo completo'),
 (2, 'GERENTE', 'Gerente', 'Gestão operacional e relatórios'),
 (3, 'CAIXA', 'Caixa', 'Operação de pagamentos e caixa'),
 (4, 'COZINHA', 'Cozinha', 'Fila de produção'),
 (5, 'GARCOM', 'Garçom', 'Atendimento de mesas'),
 (6, 'MOTOBOY', 'Motoboy', 'Entregas');

INSERT INTO permissions (id, code, description) VALUES
 (1, 'ORDERS_READ', 'Consultar pedidos'),
 (2, 'ORDERS_MANAGE', 'Criar e atualizar pedidos'),
 (3, 'CATALOG_MANAGE', 'Gerenciar catálogo'),
 (4, 'KITCHEN_MANAGE', 'Operar fila de cozinha'),
 (5, 'CASH_MANAGE', 'Operar caixa e pagamentos'),
 (6, 'DELIVERY_MANAGE', 'Gerenciar entregas'),
 (7, 'SETTINGS_MANAGE', 'Gerenciar configurações'),
 (8, 'USERS_MANAGE', 'Gerenciar usuários e permissões');

INSERT INTO role_permissions (role_id, permission_id)
SELECT roles.id, permissions.id FROM roles CROSS JOIN permissions WHERE roles.code = 'ADMIN';
INSERT INTO role_permissions (role_id, permission_id) SELECT 2, id FROM permissions WHERE code IN ('ORDERS_READ', 'ORDERS_MANAGE', 'CATALOG_MANAGE', 'KITCHEN_MANAGE', 'CASH_MANAGE', 'DELIVERY_MANAGE');
INSERT INTO role_permissions (role_id, permission_id) SELECT 3, id FROM permissions WHERE code IN ('ORDERS_READ', 'CASH_MANAGE');
INSERT INTO role_permissions (role_id, permission_id) SELECT 4, id FROM permissions WHERE code IN ('ORDERS_READ', 'KITCHEN_MANAGE');
INSERT INTO role_permissions (role_id, permission_id) SELECT 5, id FROM permissions WHERE code IN ('ORDERS_READ', 'ORDERS_MANAGE');
INSERT INTO role_permissions (role_id, permission_id) SELECT 6, id FROM permissions WHERE code IN ('ORDERS_READ', 'DELIVERY_MANAGE');

INSERT INTO restaurant_tables (id, number, capacity, active, qr_token) VALUES
 (1, '01', 4, TRUE, 'jack-table-01'), (2, '02', 4, TRUE, 'jack-table-02'),
 (3, '03', 4, TRUE, 'jack-table-03'), (4, '04', 4, TRUE, 'jack-table-04'),
 (5, '05', 6, TRUE, 'jack-table-05');

INSERT INTO table_qr_tokens (table_id, token, active) VALUES
 (1, 'jack-table-01', TRUE), (2, 'jack-table-02', TRUE), (3, 'jack-table-03', TRUE),
 (4, 'jack-table-04', TRUE), (5, 'jack-table-05', TRUE);

INSERT INTO product_inventory (product_id, track_inventory) SELECT id, FALSE FROM products;

INSERT INTO system_settings (setting_key, setting_value) VALUES
 ('restaurant.name', 'Jack Burguer'),
 ('order.delivery_fee', '6.00'),
 ('order.delivery_estimate', '35 a 50 min'),
 ('order.pickup_estimate', '20 a 30 min');
