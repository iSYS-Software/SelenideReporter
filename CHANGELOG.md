     ____       _            _     _         ____                       _            
    / ___|  ___| | ___ _ __ (_) __| | ___   |  _ \ ___ _ __   ___  _ __| |_ ___ _ __ 
    \___ \ / _ \ |/ _ \ '_ \| |/ _` |/ _ \  | |_) / _ \ '_ \ / _ \| '__| __/ _ \ '__|
     ___) |  __/ |  __/ | | | | (_| |  __/  |  _ <  __/ |_) | (_) | |  | ||  __/ |   
    |____/ \___|_|\___|_| |_|_|\__,_|\___|  |_| \_\___| .__/ \___/|_|   \__\___|_|   
                                                      |_|                            
# Changelog

The format of this file is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [Unreleased]


## [2.0.0] - 2024-02-29

### Changed
- updated to Gradle 8 and JUnit 5 (breaking change: you must upgrade your app to JUnit5 as well)


## [1.1.7] - 2023-11-02

### Added
- configuration parameters for up to three external API URLs


## [1.1.6] - 2023-09-05

### Added
- configure webdriver.chrome.driver with default /usr/bin/chromedriver

### Changed
- removed release goals from build.gradle


## [1.1.5] - 2023-09-04

### Added
- define minimum version constraint for httpclient5


## [1.1.4] - 2023-06-23

### Fixed
- replace all invalid filename characters in screenshot image names


## [1.1.3] - 2023-06-22

### Fixed
- path to screenshot images is now restored from JSON archive


## [1.1.2] - 2023-06-22

### Changed
- path to screenshot images is now relative to make report self-contained


## [1.1.1] - 2023-06-17

### Fixed
- set start and end timestamp when running tests
- new version of extentreports-java that preserves timestamps between tests
- do not overwrite screenshots, make image file names more likely to be unique

### Changed
- use screenshot facility of Selenide instead of Selenium


## [1.1.0] - 2023-05-23

### Added
- Java 11 Support through FileAdapter mapping


## [1.0.9] - 2023-05-22

### Changed
- updated dependencies: extentreports-java, Selenide, selenium-http-jdk-client


## [1.0.8] - 2023-05-03

### Changed
- update GSON dependency
- introduce System Property APPEND.TO.EXISTING.REPORT to switch off JsonReporter
- safer code execution by introducing several try/catch blocks


## [1.0.7] - 2023-04-24

### Changed
- add newest version of httpclient5 to prevent dependency clash with Spring Boot


## [1.0.6] - 2023-04-24

### Changed
- bump Selenide to 6.13.0
- use Java 11+ Http client to support Chrome 111+
- bump minimum Java version to 11


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


[unreleased]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/2.0.0...main?from_project_id=143
[2.0.0]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.1.7...2.0.0?from_project_id=143
[1.1.7]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.1.6...1.1.7?from_project_id=143
[1.1.6]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.1.5...1.1.6?from_project_id=143
[1.1.5]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.1.4...1.1.5?from_project_id=143
[1.1.4]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.1.3...1.1.4?from_project_id=143
[1.1.3]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.1.2...1.1.3?from_project_id=143
[1.1.2]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.1.1...1.1.2?from_project_id=143
[1.1.1]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.1.0...1.1.1?from_project_id=143
[1.1.0]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.9...1.1.0?from_project_id=143
[1.0.9]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.8...1.0.9?from_project_id=143
[1.0.8]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.7...1.0.8?from_project_id=143
[1.0.7]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.6...1.0.7?from_project_id=143
[1.0.6]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.5...1.0.6?from_project_id=143
[1.0.5]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.4...1.0.5?from_project_id=143
[1.0.4]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.3...1.0.4?from_project_id=143
[1.0.3]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.2...1.0.3?from_project_id=143
[1.0.2]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.1...1.0.2?from_project_id=143
[1.0.1]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/compare/1.0.0...1.0.1?from_project_id=143
[1.0.0]: https://gitlab.isys.de/ulrichmayring/selenide-reporter/-/tags/1.0.0
