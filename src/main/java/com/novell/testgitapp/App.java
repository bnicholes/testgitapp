package com.novell.testgitapp;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.PageViewTelemetry;
import com.microsoft.applicationinsights.telemetry.SessionState;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        
        MainAppClass mainAppClass = new MainAppClass();
        mainAppClass.runTelemetryTest();
    }
}


class MainAppClass {
    private TelemetryClient telemetry;
    private List<Thread> threads = new ArrayList<Thread>();

    public TelemetryClient getTelemetry() {
        return telemetry;
    }

    public List<Thread> getThreads() {
        return threads;
    }
    
    public void runTelemetryTest() { 
        
        for (SessionState c : SessionState.values())
            System.out.println(c);

        telemetry = new TelemetryClient();
        telemetry.getContext().getProperties().put("AppName", "TestGitApp");
        telemetry.getContext().getProperties().put("AppVersion", "0.72");
        

        EventThread eventThread = new EventThread(this);
        threads.add(eventThread);
        eventThread.start();
        MetricThread metricThread = new MetricThread(this);
        threads.add(metricThread);
        metricThread.start();
        MultiMetricEventThread multiMetricEventThread = new MultiMetricEventThread(this);
        threads.add(multiMetricEventThread);
        multiMetricEventThread.start();
        PageViewThread pageViewThread = new PageViewThread(this);
        threads.add(pageViewThread);
        pageViewThread.start();
        
        while (threads.size() > 0) {
            try {
                System.out.println("About to wait...");
                threads.get(0).join();
                System.out.println("Done waiting...");
            } catch (InterruptedException ex) {}
        }
    }
}

class EventThread extends Thread {

    private MainAppClass mainAppClass;
    
    public EventThread(MainAppClass mainAppClass) {
        this.mainAppClass = mainAppClass;
    }
    
    @Override
    public void run() {
        TelemetryClient telemetry = mainAppClass.getTelemetry();
        long timeIn = new Date().getTime();
        
        for (int x=0; x < 25; x++) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {}
            telemetry.trackEvent("byEvent");
        }
        long timeOut = new Date().getTime();
        telemetry.trackDependency("EventThread", "EventThread", new Duration(timeOut-timeIn), true);
        mainAppClass.getThreads().remove(this);
    }
}

class MultiMetricEventThread extends Thread {

    private MainAppClass mainAppClass;
    
    public MultiMetricEventThread(MainAppClass mainAppClass) {
        this.mainAppClass = mainAppClass;
    }
    
    @Override
    public void run() {
        TelemetryClient telemetry = mainAppClass.getTelemetry();
        Map<String,String> eventProps = new HashMap<String,String>();
        Map<String,Double> metrics = new HashMap<String,Double>();
        Random rand = new Random();
        long timeIn = new Date().getTime();
        
        eventProps.put("Type", "Event");
        eventProps.put("Name", "MultiMetricEventThread");
        
        for (int x=0; x < 25; x++) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {}
            metrics.clear();
            metrics.put("RandomValue1", (rand.nextInt(51)*1.0));
            metrics.put("RandomValue2", (rand.nextInt(51)*1.0));
            metrics.put("RandomValue3", (rand.nextInt(51)*1.0));
            telemetry.trackEvent("multiEvent", eventProps, metrics);
        }
        long timeOut = new Date().getTime();
        telemetry.trackDependency("MultiMetricEventThread", "MultiMetricEventThread", new Duration(timeOut-timeIn), true);
        mainAppClass.getThreads().remove(this);
    }
}

class MetricThread extends Thread {

    private MainAppClass mainAppClass;
    
    public MetricThread(MainAppClass mainAppClass) {
        this.mainAppClass = mainAppClass;
    }
    
    @Override
    public void run() {
        TelemetryClient telemetry = mainAppClass.getTelemetry();
        Random rand = new Random();
        long timeIn = new Date().getTime();
        
        for (int x=0; x < 25; x++) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {}
            int randomNum = rand.nextInt(51);
            telemetry.trackMetric("randomMetric", randomNum);
        }
        long timeOut = new Date().getTime();
        telemetry.trackDependency("MetricThread", "MetricThread", new Duration(timeOut-timeIn), true);
        mainAppClass.getThreads().remove(this);
    }
}

class PageViewThread extends Thread {

    private MainAppClass mainAppClass;
    
    public PageViewThread(MainAppClass mainAppClass) {
        this.mainAppClass = mainAppClass;
    }
    
    @Override
    public void run() {
        TelemetryClient telemetry = mainAppClass.getTelemetry();
        Random rand = new Random();
        
        long timeIn = new Date().getTime();
                
        for (int x=0; x < 25; x++) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {}
            PageViewTelemetry pageViewTelemetry = new PageViewTelemetry("pageViewMetric");
            pageViewTelemetry.setDuration(rand.nextInt(101));
            telemetry.trackPageView(pageViewTelemetry);
        }
        long timeOut = new Date().getTime();
        telemetry.trackDependency("PageViewThread", "PageViewThread", new Duration(timeOut-timeIn), true);
        mainAppClass.getThreads().remove(this);
    }
}
