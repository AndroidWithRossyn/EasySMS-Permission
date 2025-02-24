# Changelog

## [1.0.1.a0] - 2025-01-15

### Added

- Implemented `saveBulkSmsToFirestore` method in FirestoreHelper to save multiple SMS messages in a
  batch.
- Added SharedPreferences integration to ensure bulk messages are saved only once.
- Introduced a flag (`isBulkUploaded`) to track the upload status and prevent duplicate uploads.
- Updated FirestoreHelper to use device-specific collection naming for better organization.
- Enhanced dependency injection with SharedPreferences using Dagger Hilt.
- Added error handling and success callbacks for bulk save operations.
- Refactored code for improved modularity and readability.

## [1.0.0.a1] - 2025-01-15

### Added

- Migrated to ViewModel and Repository pattern with Dagger Hilt for dependency injection.
- Integrated Dagger Hilt for seamless dependency management.
- Generated documentation using Dokka.
- Implemented DI architecture for better scalability and maintainability.
- Added SMS permission handling functionality.
- Displayed SMS messages in a simple list format.

### Note

- This commit enhances the project structure with a more modular and scalable architecture using
  ViewModel, Repository, and Dagger Hilt.

## [1.0.0] - 2025-01-15

### Added

- Basic project setup with initial activity.
- Set up app structure and dependencies.

### Note

- This is the initial commit to set up the basic functionality of the app.
