package com.ekcapaper.racingar.activity.raar;

public interface ActivityInitializer {
    default void initActivity(){
        initActivityField();
        initActivityComponent();
        initActivityEventTask();
    }
    void initActivityField();
    void initActivityComponent();
    void initActivityEventTask();
}
