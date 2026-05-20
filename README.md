# Cart Service

Microservicio de carrito de compras. Permite agregar, actualizar y eliminar productos del carrito de un usuario, validando disponibilidad de stock en tiempo real.

## Información general

| Campo | Valor |
|-------|-------|
| Puerto | `8086` |
| Base de datos | `db_cart` (PostgreSQL) |
| Contexto | `/api/cart` |

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/cart/user/{userId}` | Ver carrito de un usuario |
| `POST` | `/api/cart` | Agregar producto al carrito |
| `PUT` | `/api/cart/{id}` | Actualizar cantidad de un ítem |
| `DELETE` | `/api/cart/{id}` | Eliminar ítem del carrito |
| `DELETE` | `/api/cart/user/{userId}` | Vaciar carrito completo |

## Ejemplo de uso

**Agregar al carrito:**
```bash
curl -X POST http://localhost:8086/api/cart \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 1,
    "quantity": 2
  }'
```

**Respuesta:**
```json
{
  "id": 1,
  "userId": 1,
  "productId": 1,
  "quantity": 2
}
```

**Ver carrito:**
```bash
curl http://localhost:8086/api/cart/user/1
```

## Modelo de datos

```sql
CREATE TABLE cart_items (
    id         BIGINT  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT  NOT NULL,
    product_id BIGINT  NOT NULL,
    quantity   INTEGER NOT NULL
);
```

## Dependencias externas

| Servicio | Uso | Puerto |
|---------|-----|--------|
| **productos** | Valida que el producto exista | `8081` |
| **inventory** | Verifica disponibilidad de stock | `8085` |

## Configuración (variables de entorno Docker)

| Variable | Descripción |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | URL de conexión a PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos |
| `FEIGN_CLIENT_PRODUCT_URL` | URL del servicio de productos |
| `FEIGN_CLIENT_INVENTORY_URL` | URL del servicio de inventario |

## Tecnologías

- Java 25 · Spring Boot 4.0.6
- Spring Data JPA · Hibernate 7
- Spring Cloud OpenFeign
- Flyway (migraciones)
- PostgreSQL 16
- Lombok · Bean Validation
