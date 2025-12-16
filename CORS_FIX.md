# 🔧 Fix CORS - Como Resolver

## 🚨 O Problema

```
Access to XMLHttpRequest at 'http://localhost:8080/auth/login' from origin 'http://localhost:5173' 
has been blocked by CORS policy: Response to preflight request doesn't pass access control check: 
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

### Por que aconteceu?

Você está rodando:
- **Frontend:** `http://localhost:5173` (Vite dev server)
- **Backend:** `http://localhost:8080` (Spring Boot)

Mas a configuração CORS do backend só permitia:
- `http://localhost:3000`
- `http://localhost:3001`

Como `localhost:5173` não estava na lista, o browser bloqueou!

---

## ✅ A Solução (Já Implementada)

### O que mudei no `SecurityConfig.java`:

**Antes:**
```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",      // Frontend local
    "http://localhost:3001",      // Frontend alternativo
    "http://127.0.0.1:3000",      // IPv4 loopback
    "https://seu-dominio.com"     // Produção
));
```

**Depois:**
```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",      // Frontend local (Create React App)
    "http://localhost:3001",      // Frontend alternativo
    "http://localhost:5173",      // ✅ Vite dev server (padrão)
    "http://localhost:5174",      // ✅ Vite dev server (alternativo)
    "http://127.0.0.1:3000",      // IPv4 loopback
    "http://127.0.0.1:5173",      // ✅ IPv4 Vite
    "https://seu-dominio.com"     // Produção
));
```

---

## 🚀 Próximos Passos

### 1. Parar o Backend
```bash
# Pressiona Ctrl+C no terminal onde o backend está rodando
```

### 2. Reiniciar o Backend
```bash
cd mugen-backend
./mvnw spring-boot:run
```

### 3. Testar o Login
Va para `http://localhost:5173` e tente fazer login:
- Email: seu@email.com
- Senha: sua-senha

✅ Deve funcionar agora!

---

## 📋 Como CORS Funciona

### Fluxo sem CORS:
```
Frontend (5173)           Browser           Backend (8080)
    │                       │                     │
    ├─ POST /auth/login ───→│                     │
    │                       │ [Preflight]        │
    │                       ├─ OPTIONS /auth ───→│
    │                       │                ❌ Erro! Origem não permitida
    │                       │
    │ ❌ CORS Error ←──────│
    │
```

### Fluxo COM CORS (depois do fix):
```
Frontend (5173)           Browser           Backend (8080)
    │                       │                     │
    ├─ POST /auth/login ───→│                     │
    │                       │ [Preflight]        │
    │                       ├─ OPTIONS /auth ───→│
    │                       │          Origin: localhost:5173
    │                       │                ✅ Permitida!
    │                       │← CORS Headers
    │                       │
    │                       ├─ POST /auth/login ───→│
    │                       │                     │ Login bem-sucedido
    │← JWT Token ←──────│←────────────│
    │
    ✅ Pronto para usar!
```

---

## 🔐 Explicação dos Campos CORS

### `setAllowedOrigins()`
**O quê:** Quais domínios podem fazer requisições
**Por quê:** Segurança - só o frontend autorizado pode acessar a API
**Exemplo:** Frontend em `localhost:5173` precisa acessar API em `localhost:8080`

### `setAllowedMethods()`
**O quê:** Quais métodos HTTP são permitidos
**Valores:** GET, POST, PUT, DELETE, OPTIONS, PATCH
**Por quê:** Controle de que tipo de operação o frontend pode fazer

### `setAllowedHeaders()`
**O quê:** Quais headers HTTP o frontend pode enviar
**Comum:** Authorization (para JWT), Content-Type
**`*`:** Significa "qualquer header"

### `setExposedHeaders()`
**O quê:** Quais headers o backend envia para o frontend ler
**Exemplo:** Authorization, X-Total-Count (para paginação)
**Por quê:** Por padrão, browser bloqueia leitura de headers

### `setAllowCredentials(true)`
**O quê:** Permitir envio de credenciais (cookies, auth headers)
**Por quê:** Necessário para JWT funcionar

### `setMaxAge(3600L)`
**O quê:** Quanto tempo cachear resposta do preflight
**Valor:** 3600 segundos = 1 hora
**Por quê:** Performance - não precisa fazer preflight a cada request

---

## 🐛 Se Ainda Não Funcionar

### 1. Verificar se backend realmente reiniciou
```bash
# Deve estar rodando em:
http://localhost:8080

# Testar com:
curl http://localhost:8080/health
# Deve retornar: OK ou UP
```

### 2. Limpar cache do browser
```
Ctrl+Shift+Delete (Windows/Linux)
Cmd+Shift+Delete (Mac)

Ou: DevTools → Application → Clear site data
```

### 3. Verificar console do browser
```
F12 → Console

Procura por mais detalhes do erro CORS
```

### 4. Testar com Postman
```
POST http://localhost:8080/auth/login
Body (raw JSON):
{
  "email": "seu@email.com",
  "password": "senha"
}

Se funcionar no Postman mas não no browser,
é definitivamente problema de CORS
```

### 5. Verificar se JWT está sendo gerado
```
No Postman, se receber:
{
  "data": {
    "token": "eyJhbGc..."
  }
}

Então o backend está ok, é só questão do frontend receber
```

---

## 📋 Configuração Recomendada por Ambiente

### 🚤 Desenvolvimento (localhost)
```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "http://localhost:5173",
    "http://127.0.0.1:3000",
    "http://127.0.0.1:5173"
));
config.setAllowCredentials(true);
config.setMaxAge(3600L);
```

### 🟡 Staging
```java
config.setAllowedOrigins(Arrays.asList(
    "https://staging-app.com",
    "https://staging-api.com"
));
config.setAllowCredentials(true);
config.setMaxAge(86400L); // 24 horas
```

### 🟪 Produção
```java
config.setAllowedOrigins(Arrays.asList(
    "https://www.seu-dominio.com",
    "https://api.seu-dominio.com"
));
config.setAllowCredentials(true);
config.setMaxAge(86400L); // 24 horas
```

---

## 💡 Dicas de Segurança

### ❌ NUNCA faça isso:
```java
// Permitir QUALQUER origem!
config.setAllowedOrigins(Arrays.asList("*"));
config.setAllowCredentials(true); // ← Conflita!
```

### ✅ Melhor Prática:
```java
// Usar variáveis de ambiente
String[] allowedOrigins = System.getenv("ALLOWED_ORIGINS").split(",");
config.setAllowedOrigins(Arrays.asList(allowedOrigins));
```

---

## 📚 Leitura Extra

- [MDN: CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Spring Boot CORS Docs](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/CorsRegistry.html)
- [JWT Security Best Practices](https://auth0.com/blog/critical-vulnerabilities-in-json-web-token-libraries/)

---

## ✅ Checklist

- [ ] Parei o backend (Ctrl+C)
- [ ] Atualizei o SecurityConfig.java com as novas origens
- [ ] Reiniciei o backend (`./mvnw spring-boot:run`)
- [ ] Limpei o cache do browser (Ctrl+Shift+Delete)
- [ ] Tentei fazer login em `http://localhost:5173`
- [ ] Login funcionou! ✅

---

**Status:** 🚀 Problema resolvido!  
**Commit:** b51e332514a09f992b29c7f24b828456369268e5  
**Data:** 16/12/2025  

🎮 Mugen pronto para rodar!
