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
            report.pass("All good", false);
        });
    }

}
```
