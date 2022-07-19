     ____       _            _     _         ____                       _            
    / ___|  ___| | ___ _ __ (_) __| | ___   |  _ \ ___ _ __   ___  _ __| |_ ___ _ __ 
    \___ \ / _ \ |/ _ \ '_ \| |/ _` |/ _ \  | |_) / _ \ '_ \ / _ \| '__| __/ _ \ '__|
     ___) |  __/ |  __/ | | | | (_| |  __/  |  _ <  __/ |_) | (_) | |  | ||  __/ |   
    |____/ \___|_|\___|_| |_|_|\__,_|\___|  |_| \_\___| .__/ \___/|_|   \__\___|_|   
                                                      |_|                            
# Changelog

The format of this file is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [Unreleased]


## [1.0.5] - 2022-07-22

###  Fixed
- catch Throwable in UiTestReport to make sure it can still be written in case of fatal error


## [1.0.4] - 2022-03-07

### Changed
- bump Selenide version to 6.2.1
- general cleanup for first release on Github


## [1.0.3] - 2022-01-24

### Changed
- Task release now publishes to iSYS Nexus Maven Repo
- new version of forked Extent-Reports that provides [this enhancement](https://github.com/extent-framework/extentreports-java/issues/329)
- support Java 8


## [1.0.2] - 2022-01-17

### Added
- This Changelog file
- Task release in build.gradle


## [1.0.1] - 2021-12-20

### Fixed
- Use forked version of Extent-Reports that fixes [this bug](https://github.com/extent-framework/extentreports-java/issues/333)

### Changed
- Restructured README.md to make it more readable

### Removed
- TODO-Markers for completed tasks


## [1.0.0] - 2021-12-16

### Added
- Initial release


[unreleased]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.5...main?from_project_id=143
[1.0.5]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.4...1.0.5?from_project_id=143
[1.0.4]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.3...1.0.4?from_project_id=143
[1.0.3]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.2...1.0.3?from_project_id=143
[1.0.2]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.1...1.0.2?from_project_id=143
[1.0.1]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.0...1.0.1?from_project_id=143
[1.0.0]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/tags/1.0.0
