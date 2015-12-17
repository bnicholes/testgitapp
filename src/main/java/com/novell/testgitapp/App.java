package com.novell.testgitapp;

import com.microsoft.applicationinsights.TelemetryClient;

public class App {

    private static TelemetryClient telemetry = new TelemetryClient();
    
    public static void main(String[] args) {
        System.out.println("Hello World!");
        
        telemetry.getContext().setInstrumentationKey("4ddd203b-b96e-43e1-b781-c5efc6224be5");
        
//        EventTelemetry eventTelemetry = new EventTelemetry();
//        eventTelemetry.
        for (int x=0; x < 50; x++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {}
            telemetry.trackEvent("byEvent");
        }
        
        telemetry.
    }
}
