package tests;

// Simple no-framework test runner with per-test pass/fail output.
public class TestRunner {
    public static void main(String[] args) {
        ServiceTests tests = new ServiceTests();
        int passed = 0;
        int failed = 0;

        if (runTest("testAddAndGetFlow", tests::testAddAndGetFlow)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("testNotFoundExceptions", tests::testNotFoundExceptions)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("testBillSummaryGeneration", tests::testBillSummaryGeneration)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("testCsvPersistenceReload", tests::testCsvPersistenceReload)) {
            passed++;
        } else {
            failed++;
        }

        if (runTest("testRecommendationService", tests::testRecommendationService)) {
            passed++;
        } else {
            failed++;
        }

        System.out.println("--------------------------------");
        System.out.println("Total: " + (passed + failed));
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            System.exit(1);
        }
    }

    // Executes one test method and converts thrown errors to status output.
    private static boolean runTest(String name, Runnable test) {
        try {
            test.run();
            System.out.println("[PASS] " + name);
            return true;
        } catch (AssertionError e) {
            System.out.println("[FAIL] " + name + " -> " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("[ERROR] " + name + " -> " + e.getMessage());
            return false;
        }
    }
}
