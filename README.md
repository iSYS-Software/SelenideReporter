# Selenide Reporter

A reporting framework for the Selenide UI testing tool.

## Elevator Pitch

[Selenide](https://selenide.org/) is a fluent API for [Selenium Webdriver](https://docs.seleniumhq.org/projects/webdriver/) and probably the best way to write concise UI tests in Java. But in terms of reporting functionality it relies on generic tooling and some [basic integrations](https://selenide.org/documentation/reports.html).

Therefore this framework aims to add the following features to Selenide:
- opinionated reporting: a way to structure your tests.
- custom reporting: you state explicitly what should be in the report.
- modern visuals (as provided by [Extent Reports](https://github.com/extent-framework/extentreports-java)).
- self-contained reports (so you can easily publish them on a CI server).

## Installation
Include SelenideReporter into your project like so (assuming 1.1.6 to be the version you want - see [Releases](../../releases) for the most current version):

    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }

    dependencies {
        ....
        testImplementation 'com.github.iSYS-Software:SelenideReporter:1.1.6'
    }

This will pull in a current version of Selenide (>= 6). If you are also using Spring Boot, it is possible that it 
pulls in older versions of some Selenium jars, which will break Selenide. So make sure that 
`org.seleniumhq.selenium:selenium-java` and all its transitive dependencies have version >= 4. Spring Boot might 
pull in v3.141.59. An easy way to fix this would be to put a reasonably current version like 
`selenium.version=4.11.0` into your gradle.properties. For the same reason you should make sure that 
`org.apache.httpcomponents.client5:httpclient5` has at least version 5.2.1, otherwise you can again configure 
`httpclient5.version=5.2.1` in your gradle.properties.

Additionally, if you want to run your tests via Chrome, you need to install chromedriver locally at 
`/usr/bin/chromedriver` (or configure another location, see below under Configuration). If you want to use other 
browsers, you may need to install their corresponding webdriver or the browser (possibly in a headless version).

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

Run this test via `gradlew -x check clean test --tests tests.HelloWorldTest` and a report will be produced in the `build/reports/selenide` directory. If you want to generate a report for all tests in the project, simply run `gradlew clean test`. Here is a **[screenshot](samples/screenshot.png)** of what the report will look like.

## Step by Step
There's a lot going on in the HelloWorld test and this section explains it in more detail.
1. Your test class should extend `UITest` or a base class you write that extends `UITest`.
2. Annotate the class with your fallback base URL, unless http://localhost:8080 is to your liking.
3. JUnit5 is used as a test runner, so use JUnit Jupiter annotations to drive your test.
4. Start a test (and a report) with `this.run("Test Name, "longer Test Description", settings -> { ... your test code ... });`
    - settings will provide you with some configuration options you can change on the fly, e. g. setting the timeout or the size of the browser window
5. `BASE_URL` will hold your configured base URL. It will be the same URL for all methods within a test class.
6. You write to the report via methods like these:
    - report.info("my text"); // will automatically produce a screenshot
    - report.info("my text", false); // will not produce a screenshot
    - report.warning("some warning");
    - report.fail("error description");
    - report.pass("success message - your test method has completed successfully");

## Configuration
The reports are configured via System Properties, which comes in handy when running on a CI server, where configuration must be provided externally. For local development you can rely on "convention over configuration" and provide fallbacks, where the defaults aren't reasonable. The way to do this is via an annotation (see example above). However, please note that fallbacks aren't implemented for all configuration parameters yet. If you use a fallback, you can still override it in a CI build by setting the corresponding System Property. If neither System Property nor fallback are specified, a default value is applied.

| System Property | Description | Fallback Annotation implemented | Default Value |
| ------ | ------ | ------ | ------ |
| `BASE.URL` | URL where the application to test is running. | `@Fallbacks(baseUrl="https://...")` | `http://localhost:8080` |
| `API.URL.1` | URL of an external API that is needed in the test. | `@Fallbacks(apiUrl1="https://...")` | `null` |
| `API.URL.2` | If a second API is needed in the test. | `@Fallbacks(apiUrl2="https://...")` | `null` |
| `API.URL.3` | If a third API is needed in the test. | `@Fallbacks(apiUrl2="https://...")` | `null` |
| `REPORT.UITEST.DIR` | Directory where the reports are written. | no | `build/reports/selenide` |
| `UITEST.BROWSER` | Browser to use for UI tests. | no | `chrome` |
| `UITEST.BROWSER.LANG` | Browser language to use for UI tests. | no | `en` |
| `webdriver.chrome.driver` | Location of chromedriver when using Chrome. | no | `/usr/bin/chromedriver` |
| `org.slf4j.simpleLogger.defaultLogLevel` | Log level for Selenide. | no | `info` |

## Advanced Usage
There's a more involved example included [here](src/test/java/tests/ExampleTest.java).

#### Clean up after the Test
When one of your Selenide asserts fails, the test will stop immediately. If you want to run some cleanup code after the test completes (whether successfully or not), you can do it like so:

```
this.run("My Test", settings -> {
    // some error might occur here
    report.pass("All good");
}, result -> {
    // This is called in any case after the test has finished
    if (result.getThrowable() != null) {
        // something was thrown
    }
});
```

#### Use collapsible Sections
A section is a collapsible part of your test, you can use it to structure your report.

```
report.startSection("Header");
// check header
report.pass("Header Section ok"); // PASS or FAIL ends the section

report.startSection("Body");
// check Body
report.pass("Body Section ok");
```

#### Work with restmail.net
Sometimes your application sends emails and you might want to test whether they are sent correctly. You can use https://restmail.net for that and then retrieve the mails from your test. Look at the javadocs for
- checkRestMail
- clickLinkInRestMail
- deleteRestMail

#### Standard Macros
The class StandardMacros provides all helper functions to work with Selenide and you can access them all from your class extending UITest.

