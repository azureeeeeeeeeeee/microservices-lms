# Microservices gRPC Error Resolution Documentation

This document records the diagnosis, root causes, fixes, and verification for gRPC errors encountered during inter-service communication between `course-service` and `user-service`.

---

## 1. Issue #1: `NoSuchMethodError` in Netty Client Transport

### Error Log
```log
course-service   | 2026-07-25T00:23:39.346Z  WARN 83 --- [course-service] [-worker-ELG-1-2] i.g.n.s.i.n.u.concurrent.DefaultPromise  : An exception was thrown by io.grpc.netty.shaded.io.grpc.netty.NettyClientTransport$5.operationComplete()
course-service   |
course-service   | java.lang.NoSuchMethodError: 'void io.grpc.internal.ManagedClientTransport$Listener.transportShutdown(io.grpc.Status)'
course-service   |      at io.grpc.netty.shaded.io.grpc.netty.ClientTransportLifecycleManager.notifyGracefulShutdown(ClientTransportLifecycleManager.java:64) ~[grpc-netty-shaded-1.69.0.jar:1.69.0]
```

### Root Cause Analysis
> [!IMPORTANT]
> A **`NoSuchMethodError`** indicates a **classpath dependency version mismatch** between gRPC library components.

1. `grpc-netty-shaded` was explicitly set to **`1.69.0`** in `pom.xml`.
2. `grpc-core` and `grpc-api` were unversioned in `pom.xml` and inherited version **`1.80.0`** from Spring Boot parent dependency management.
3. When `grpc-netty-shaded` (v1.69.0) attempted to call internal method `transportShutdown()` on `grpc-core` (v1.80.0), the JVM threw `NoSuchMethodError` due to internal SPI refactoring between versions.

### Solution Applied
Imported `io.grpc:grpc-bom` (v`1.69.0`) in `<dependencyManagement>` of both [course-service/pom.xml](file:///d:/Project%20Coding/Portofolio/microservices-lms/course-service/pom.xml) and [user-service/pom.xml](file:///d:/Project%20Coding/Portofolio/microservices-lms/user-service/pom.xml).

---

## 2. Issue #2: `StatusRuntimeException: UNAVAILABLE: io exception`

### Error Log
```log
course-service   | 2026-07-25T00:36:39.590Z ERROR 98 --- [course-service] [nio-8002-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: io.grpc.StatusRuntimeException: UNAVAILABLE: io exception] with root cause
```

### Root Cause Analysis
> [!WARNING]
> An **`UNAVAILABLE: io exception`** (Connection Refused / Channel Unavailable) occurs when the gRPC client attempts to connect to the wrong target address, wrong port, or uses an incompatible channel negotiation type (TLS vs Plaintext).

1. **Missing Docker Compose Environment Variables in Production**:
   - `course-service/docker-compose.production.yml` was missing `GRPC_CLIENT_USER_SERVICE_ADDRESS` and `GRPC_CLIENT_USER_SERVICE_NEGOTIATION_TYPE`.
   - Without these environment variables, the gRPC client defaulted to `static://localhost:9090` using `TLS`.
2. **Port Mismatch & Negotiation Type**:
   - `user-service` listens for gRPC calls on port **`5000`** (configured via `GRPC_SERVER_PORT=5000`).
   - The gRPC channel required unencrypted HTTP/2 (`plaintext`) negotiation when communicating across the internal Docker network.

### Solution Applied

1. **`course-service/docker-compose.production.yml`**: Added gRPC client environment variables to align with production container networking:
   ```yaml
         - GRPC_CLIENT_USER_SERVICE_ADDRESS=static://user-service:5000
         - GRPC_CLIENT_USER_SERVICE_NEGOTIATIONTYPE=plaintext
         - GRPC_CLIENT_USER_SERVICE_NEGOTIATION_TYPE=plaintext
   ```

2. **`course-service/docker-compose.development.yml`**: Added `GRPC_CLIENT_USER_SERVICE_NEGOTIATION_TYPE` alongside `GRPC_CLIENT_USER_SERVICE_NEGOTIATIONTYPE` for full Spring Boot environment binding compatibility.

3. **Fallback Application Properties**:
   - Added default gRPC client properties in [course-service/src/main/resources/application.properties](file:///d:/Project%20Coding/Portofolio/microservices-lms/course-service/src/main/resources/application.properties):
     ```properties
     grpc.client.user-service.address=static://user-service:5000
     grpc.client.user-service.negotiation-type=plaintext
     ```
   - Added default gRPC server port in [user-service/src/main/resources/application.properties](file:///d:/Project%20Coding/Portofolio/microservices-lms/user-service/src/main/resources/application.properties):
     ```properties
     grpc.server.port=5000
     ```

---

## Verification

1. **gRPC Dependency Tree Alignment**:
   ```text
   [INFO] com.cendekia:course-service:jar:0.0.1-SNAPSHOT
   [INFO] +- io.grpc:grpc-netty-shaded:jar:1.69.0:compile
   [INFO] |  +- io.grpc:grpc-util:jar:1.69.0:runtime
   [INFO] |  +- io.grpc:grpc-core:jar:1.69.0:compile
   [INFO] |  |  \- io.grpc:grpc-context:jar:1.69.0:runtime
   [INFO] |  \- io.grpc:grpc-api:jar:1.69.0:compile
   [INFO] +- io.grpc:grpc-protobuf:jar:1.69.0:compile
   [INFO] |  \- io.grpc:grpc-protobuf-lite:jar:1.69.0:runtime
   [INFO] +- io.grpc:grpc-stub:jar:1.69.0:compile
   [INFO] \- net.devh:grpc-spring-boot-starter:jar:3.1.0.RELEASE:compile
   [INFO]    \- net.devh:grpc-server-spring-boot-starter:jar:3.1.0.RELEASE:compile
   [INFO]       +- io.grpc:grpc-inprocess:jar:1.69.0:compile
   [INFO]       \- io.grpc:grpc-services:jar:1.69.0:compile
   ```

2. **Compilation**:
   Both `course-service` and `user-service` compiled successfully (`BUILD SUCCESS`).
