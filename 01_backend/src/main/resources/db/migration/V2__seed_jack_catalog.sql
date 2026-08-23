INSERT INTO categories (id, name, description, display_order, active) VALUES (1, 'Lanches', 'Hambúrgueres artesanais Jack', 1, TRUE);

INSERT INTO products (id, category_id, name, description, bread, price, image, color, featured, available, display_order) VALUES
 (1, 1, 'Burger Bacon', 'Carne 160g, queijo prato, bacon, tomate, alface e maionese', 'Pão de hambúrguer', 29.00, '/images/menu/01-burger-bacon.jpeg', 'provided', FALSE, TRUE, 1),
 (2, 1, 'Burger Egg', 'Carne 160g, queijo prato, presunto, ovo, tomate, alface e maionese', 'Pão de hambúrguer', 28.00, '/images/menu/02-burger-egg.jpeg', 'provided', FALSE, TRUE, 2),
 (3, 1, 'Cheese Burguer', 'Carne 160g, queijo prato, requeijão (Scala), bacon, tomate, alface e maionese', 'Pão de hambúrguer', 30.00, '/images/menu/03-chesse-burguer.jpeg', 'provided', FALSE, TRUE, 3),
 (4, 1, 'Contra Filé Acebolado', '250g contra filé, queijo prato, bacon, tomate e cebola', 'Pão francês mata fome', 54.00, '/images/menu/04-contra-file-acebolado.jpeg', 'provided', FALSE, TRUE, 4),
 (5, 1, 'Double Cheese', '250g de fraldinha, queijo prato, requeijão (Scala), tomate e maionese', 'Pão francês mata fome', 46.00, '/images/menu/05-double-chesse.jpeg', 'provided', FALSE, TRUE, 5),
 (6, 1, 'Duplo Burguer', '2 carnes 160g, queijo prato, bacon, tomate, alface e maionese', 'Pão de hambúrguer', 37.00, '/images/menu/06-duplo-burguer.jpeg', 'provided', FALSE, TRUE, 6),
 (7, 1, 'Fraldinha Especial', '250g de fraldinha, queijo prato, presunto, bacon, cebola, tomate e maionese', 'Pão francês mata fome', 42.00, '/images/menu/07-fraldinha-especial.jpeg', 'provided', FALSE, TRUE, 7),
 (8, 1, 'Fraldinha', '250g de fraldinha, queijo prato, bacon, cebola e maionese', 'Pão francês mata fome', 38.00, '/images/menu/08-fraldinha.jpeg', 'provided', FALSE, TRUE, 8),
 (9, 1, 'Gorgo Burguer', 'Carne 160g, queijo prato, bacon, molho de gorgonzola, rúcula e maionese', 'Pão brioche', 35.00, '/images/menu/09-gorgo-burguer.jpeg', 'provided', FALSE, TRUE, 9),
 (10, 1, 'Jack Chilli', 'Carne 160g, queijo prato, bacon, geleia de pimenta, requeijão (Scala), rúcula e maionese', 'Pão brioche', 38.00, '/images/menu/10-jack-chilli.jpeg', 'provided', FALSE, TRUE, 10),
 (11, 1, 'Jack Salada', 'Carne 160g, queijo prato, tomate, alface e maionese', 'Pão de hambúrguer', 25.00, '/images/menu/11-jack-salada.jpeg', 'provided', FALSE, TRUE, 11),
 (12, 1, 'Rings Burguer', 'Carne 160g, queijo prato, onion rings, alface, tomate e maionese', 'Pão brioche', 33.00, '/images/menu/12-rings-burguer.jpeg', 'provided', FALSE, TRUE, 12);

INSERT INTO addons (id, name, description, price, active) VALUES
 (1, 'Bacon', 'Porção extra de bacon', 4.00, TRUE),
 (2, 'Cheddar Extra', 'Porção extra de cheddar', 3.00, TRUE),
 (3, 'Cebola Caramelizada', 'Porção extra de cebola caramelizada', 3.50, TRUE),
 (4, 'Molho Especial', 'Porção extra de molho especial', 2.00, TRUE);

INSERT INTO product_addons (product_id, addon_id)
SELECT products.id, addons.id FROM products CROSS JOIN addons;
