package org.example;

public enum TestOption {
    FailedFailed("org.example.testcase.FailedFailed"),
    KilledKilled("org.example.testcase.KilledKilled"),
    Foreach("org.example.testcase.Foreach"),
    CollectMiB("org.example.testcase.CollectMiB");

    private final String className;

    TestOption(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
