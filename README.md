# Selenide Reporter

A reporting framework for the Selenide UI testing tool.

## Elevator Pitch

[Selenide](https://selenide.org/) is a fluent API for [Selenium Webdriver](https://docs.seleniumhq.org/projects/webdriver/) and very likely the best way to write concise UI tests in Java. But in terms of reporting functionality it relies on generic tooling and some [basic integrations](https://selenide.org/documentation/reports.html).

Therefore this framework aims to add the following features to Selenide:
- opinionated reporting: a way to structure your tests.
- custom reporting: you decide what should be in the report.
- modern visuals (as provided by [Extent Reports](https://github.com/extent-framework/extentreports-java)).

## Installation
TODO

## Hello World Test
```
@Fallbacks(baseUrl="https://en.wikipedia.org/wiki/Hello%20World")
public class HelloWorldTest extends UITest {
    @Test
    public void helloWorld() {
        this.run("Hello World UI-Test", settings -> {
            open(BASE_URL);
            $(".mw-wiki-logo").should(appear);
            report.info("Wikipedia Logo found");
            // ... more tests
            report.pass("All good with " + BASE_URL, false);
        });
    }
}
```

Run this test via `gradlew -x check clean test --tests tests.HelloWorldTest` and a report very similar to **[this one](sample-reports/helloWorld/index.html)** will be produced in the `build/reports/selenide` directory. If you want to generate a report for all tests in the project, simply run `gradlew clean test`.

## Configuration
The reports are configured via System Properties, which comes in handy when running on a CI server, where configuration must be provided externally. For local development you can rely on "convention over configuration" and only provide fallbacks, where the defaults aren't reasonable. There is only one fallback implemented currently and it is the URL, where the application to test is running. Use the System Property `BASE.URL` to configure it externally and the annotation `@Fallbacks` as a fallback where the System Property is undefined (such as local development).

Further System Properties:
- `REPORT.UITEST.DIR`: directory where the reports are written, default is `build/reports/selenide`
- `UITEST.BROWSER`: browser to use for UI tests, default is `chrome`

## Step by Step
1. Your test class should extend `UITest` or a base class you write that extends `UITest`.
2. Annotate the class with your fallback base URL, unless http://localhost:8080 is to your liking.
3. JUnit is used as a test runner, so use JUnit annotations to drive your test.
4. Start a test (and a report) with `this.run("Test Name, "longer Test Description", settings -> { ... your test code ... });`
    - settings will provide you with some configuration options you can change on the fly
5. `BASE_URL` will hold your configured base URL. It will be the same URL for all methods within a test class.
6. You write to the report via:
    - report.info("my text"); // will automatically produce a screenshot
    - report.info("my text", false); // will not produce a screenshot
    - report.warning("some warning");
    - report.fail("error description");
    - report.pass("success message - your test method has completed successfully");
