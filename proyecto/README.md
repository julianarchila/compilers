# JSON Schema to TypeScript Transpiler

Una herramienta modular y extensible que convierte automáticamente JSON Schemas en definiciones de tipos TypeScript, generando código legible, seguro y consistente.

## Características

- **Pipeline modular**: JSON Schema → Parser → AST → TypeScript Generator → Output
- **AST personalizado**: Representación intermedia independiente del schema original
- **Tipos soportados**: Primitivos, objetos, arrays, tuplas, enums, const, unions, intersections
- **Código TypeScript idiomático**: Genera tipos limpios y legibles
- **100% Type-safe**: Totalmente tipado con TypeScript
- **Testing completo**: 38+ tests con 100% de cobertura de casos críticos

## Instalación

```bash
bun install
```

## Uso

### API Básica

```typescript
import { convert, jsonSchemaToTypeScript } from "./src/index.ts";

// Uso simple
const schema = {
  type: "object",
  properties: {
    name: { type: "string" },
    age: { type: "number" },
    email: { type: "string" }
  },
  required: ["name", "email"]
};

const code = convert(schema, "User");
console.log(code);
```

**Output:**
```typescript
export type User = {
  name: string;
  email: string;
  age?: number;
};
```

### Ejemplos Avanzados

#### Tipos Primitivos y Uniones
```typescript
// Union types
const schema1 = { type: ["string", "null"] };
convert(schema1); // "string | null"

// Enum
const schema2 = { enum: ["red", "green", "blue"] };
convert(schema2, "Color"); // 'export type Color = "red" | "green" | "blue";'

// Const
const schema3 = { const: "admin" };
convert(schema3); // '"admin"'
```

#### Arrays y Tuplas
```typescript
// Array homogéneo
const schema1 = {
  type: "array",
  items: { type: "string" }
};
convert(schema1); // "string[]"

// Tupla
const schema2 = {
  type: "array",
  items: [
    { type: "string" },
    { type: "number" },
    { type: "boolean" }
  ]
};
convert(schema2); // "[string, number, boolean]"
```

#### Objetos Complejos
```typescript
const schema = {
  type: "object",
  properties: {
    id: { type: "integer" },
    username: { type: "string" },
    roles: {
      type: "array",
      items: { enum: ["admin", "user", "moderator"] }
    },
    metadata: {
      type: "object",
      additionalProperties: { type: "string" }
    }
  },
  required: ["id", "username"]
};

convert(schema, "User");
```

**Output:**
```typescript
export type User = {
  id: number;
  username: string;
  roles?: ("admin" | "user" | "moderator")[];
  metadata?: {
    [key: string]: string;
  };
};
```

#### Combinadores (oneOf, anyOf, allOf)
```typescript
// oneOf - Union
const schema1 = {
  oneOf: [
    { type: "string" },
    { type: "number" }
  ]
};
convert(schema1); // "string | number"

// allOf - Intersection
const schema2 = {
  allOf: [
    {
      type: "object",
      properties: { name: { type: "string" } }
    },
    {
      type: "object",
      properties: { age: { type: "number" } }
    }
  ]
};
convert(schema2); // Intersection de ambos objetos
```

### API Avanzada

```typescript
import { jsonSchemaToTypeScript, parseSchema, generateTypeScript } from "./src/index.ts";

// Obtener el AST intermedio
const result = jsonSchemaToTypeScript(schema, { returnAST: true });
console.log(result.ast);

// Usar el parser y generador por separado
const ast = parseSchema(schema);
const code = generateTypeScript(ast, {
  typeName: "MyType",
  useTypeAlias: true,
  addComments: true
});
```

## Arquitectura

```
/src
  /ast          → Definición del AST (Abstract Syntax Tree)
  /parser       → Parser que convierte JSON Schema → AST
  /generator    → Generador que convierte AST → TypeScript
  /index.ts     → API pública
/tests          → Tests unitarios (38+ casos de prueba)
```

### AST Nodes

El AST personalizado incluye los siguientes tipos de nodos:

- `PrimitiveNode`: string, number, boolean, null
- `ObjectNode`: Objetos con propiedades y additionalProperties
- `ArrayNode`: Arrays homogéneos
- `TupleNode`: Arrays con tipos específicos por posición
- `UnionNode`: Uniones de tipos (A | B)
- `IntersectionNode`: Intersecciones de tipos (A & B)
- `EnumNode`: Valores enum
- `ConstNode`: Valores constantes
- `AnyNode`: Tipo any
- `NeverNode`: Tipo never

## Scripts

```bash
# Ejecutar tests
bun test

# Tests en modo watch
bun test:watch

# Verificar tipos
bun typecheck
```

## Keywords Soportadas

### Tipos Básicos
- `type`: string, number, integer, boolean, null, array, object
- `type: [...]`: Unions de tipos

### Objetos
- `properties`: Define propiedades del objeto
- `required`: Array de propiedades requeridas
- `additionalProperties`: Propiedades adicionales permitidas

### Arrays
- `items`: Define el tipo de elementos (objeto o array para tuplas)
- `minItems`, `maxItems`: Soportados en parsing

### Restricciones de Valores
- `enum`: Lista de valores permitidos
- `const`: Valor constante único

### Combinadores
- `oneOf`: Exactamente uno de los schemas
- `anyOf`: Uno o más de los schemas
- `allOf`: Todos los schemas combinados

## Limitaciones Actuales

- No soporta `$ref` (referencias internas o externas)
- No soporta patrones de validación complejos (pattern, format, etc.)
- No soporta condicionales (if/then/else)

## Testing

El proyecto incluye 38 tests que cubren:

- Tipos primitivos y sus variantes
- Objetos simples y complejos
- Arrays homogéneos y tuplas
- Enums y constantes
- Combinadores (oneOf, anyOf, allOf)
- Casos edge y objetos anidados
- Validación de errores

```bash
bun test
# ✓ 38 pass, 0 fail, 62 expect() calls
```

## Futuras Extensiones

- Soporte para `$ref`
- CLI completo
- Formatos de salida adicionales (Zod, JSDoc, etc.)
- Validación más estricta de schemas
- Optimización de tipos generados
- Branded types

## Licencia

MIT

## Tecnología

Este proyecto fue creado usando [Bun](https://bun.com) v1.3.2 y TypeScript 5.x.
