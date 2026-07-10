# Changelog

All notable changes to Maestro will be documented in this file.

## [1.3.0] - 2026-07-10

### Added

- Runtime RBAC integration with Permission Manager for test and test-suite execution permissions.
- Startup publication of Maestro's permission catalog and periodic refresh of the effective RBAC matrix.
- Classpath `permissions.json` catalog for registration and offline authorization fallback.

### Changed

- REST and WebSocket authorization now use the refresh-aware Permission Manager-backed permission store.
- Legacy RBAC properties are derived from the JSON catalog for framework compatibility.
- Permission Manager availability can be configured explicitly so planned outages use the local catalog.

### Removed

- The duplicated classpath `rbac.properties` permission declaration.
