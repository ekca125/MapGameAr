package com.ekcapaper.racingar.operator.checker;

public class TimeEndChecker extends EndChecker {
    EndChecker endChecker;

    public TimeEndChecker(EndChecker endChecker) {
        this.endChecker = endChecker;
    }

    @Override
    boolean isEnd() {
        return super.isEnd();
    }
}
