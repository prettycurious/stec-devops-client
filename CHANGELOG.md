<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# stec-devops-client Changelog

## [Unreleased]

## [1.0.5] - 2025-01-13

### Added

- Added support for project-level configuration.

## [1.0.4] - 2025-01-13

### Added
- Added Applications Display: Introduced a new label to show the applications list as a comma-separated string in the tool window.
- Automatic UI Update: Implemented PropertyChangeListener to auto-refresh UI components when configuration values change.
- UI Enhancements: Tool window now dynamically updates without recreating the entire content, ensuring the latest configuration is always displayed.

## [1.0.3] - 2025-01-13

### Added
- Added configuration parameter validation feature.

## [1.0.2] - 2024-12-17

### Added

- Improved the Tool Window UI layout to enhance the user experience.
- Moved the deployment logic to a background thread to prevent UI thread blocking.
- Disabled the "Deploy" button during deployment to prevent multiple trigger of deployment tasks.

## [1.0.1] - 2024-12-06

### Removed

- Delete default information

## [1.0.0] - 2024-12-05

### Added

- Implement deployment environment configuration and automated deployment functions

[Unreleased]: https://github.com/prettycurious/stec-devops-client/compare/1.0.5...HEAD
[1.0.5]: https://github.com/prettycurious/stec-devops-client/compare/1.0.4...1.0.5
[1.0.4]: https://github.com/prettycurious/stec-devops-client/compare/1.0.3...1.0.4
[1.0.3]: https://github.com/prettycurious/stec-devops-client/compare/1.0.2...1.0.3
[1.0.2]: https://github.com/prettycurious/stec-devops-client/compare/1.0.1...1.0.2
[1.0.1]: https://github.com/prettycurious/stec-devops-client/compare/1.0.0...1.0.1
[1.0.0]: https://github.com/prettycurious/stec-devops-client/releases/tag/1.0.0
