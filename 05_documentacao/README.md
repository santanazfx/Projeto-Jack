# Sistema de Pedidos — Hamburgueria Jack

Aplicação web responsiva para delivery e pedidos por mesa, baseada no documento de requisitos e no layout do Figma fornecido.

## Requisitos

- Java 17 ou superior
- Maven 3.6.3 ou superior

## Executar

```bash
mvn spring-boot:run
```

Abra `http://localhost:8080`.

## API

- `GET /api/products?category=Hambúrgueres&search=jack`
- `GET /api/categories`
- `POST /api/orders`
- `GET /api/orders/{trackingCode}`

Os pedidos são armazenados em memória nesta primeira versão. A camada `OrderService` foi separada para facilitar a troca por banco de dados e integrações com impressora, WhatsApp e pagamentos.
