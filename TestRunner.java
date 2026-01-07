package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions", "hooks"},
        plugin = {
                "pretty",
                "json:target/reports/json/cucumber.json"
        }
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}

package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import reports.ReportGenerator;

public class Hooks {

    @After
    public void afterScenario(Scenario scenario) {
        String scenarioName = scenario.getName()
                .replaceAll("[^a-zA-Z0-9]", "_");

        String jsonPath = "target/reports/json/cucumber.json";
        String scenarioJson = "target/reports/json/" + scenarioName + ".json";

        ReportGenerator.copyAndGenerate(jsonPath, scenarioJson);
    }
}

package reports;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class ReportGenerator {

    public static void copyAndGenerate(String sourceJson, String targetJson) {
        try {
            Files.copy(
                    new File(sourceJson).toPath(),
                    new File(targetJson).toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            generateCucumberReport(targetJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateCucumberReport(String jsonFilePath) {
        File reportOutputDirectory =
                new File("target/reports/html/" + new File(jsonFilePath).getName());

        List<String> jsonFiles = Collections.singletonList(jsonFilePath);

        Configuration configuration =
                new Configuration(reportOutputDirectory, "Cortex Deposit Automation");

        configuration.addClassifications("Platform", "Windows");
        configuration.addClassifications("Browser", "Edge");
        configuration.addClassifications("Site", "India");

        new ReportBuilder(jsonFiles, configuration).generateReports();
    }
}
