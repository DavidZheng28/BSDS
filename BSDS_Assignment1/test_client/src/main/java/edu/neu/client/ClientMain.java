package edu.neu.client;

import javax.ws.rs.client.ClientBuilder;
import java.util.ArrayList;
import java.util.List;

public class ClientMain {
    private List<Thread> listOfThreads = new ArrayList<>();
    final static double[] PHASE_FACTOR = {0.1, 0.5, 1, 0.25};
    final static String[] PHASE = {"Warmup", "Loading", "Peak", "Cooldown"};
    private long startT;
    private long endT;
    private long runTime;

    public long getStartT() {
        return startT;
    }

    public void setStartT(long startT) {
        this.startT = startT;
    }

    public long getEndT() {
        return endT;
    }

    public void setEndT(long endT) {
        this.endT = endT;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    private MyClient buildClient() {
        return new MyClient(ClientBuilder.newClient());
    }

    private void buildThreads(MyClient myClient, int threadCount) {
        for(int i = 0; i < threadCount; ++i) {
            Thread t = new Thread(myClient);
            listOfThreads.add(t);
        }
    }

    private void joinThreads()  {
        for(Thread t : listOfThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startThreads() {
        for(Thread t : listOfThreads) {
            t.start();
        }
    }

    private long calculateRunTime() {
        return getEndT() - getStartT();
    }

    private void printThreadNumber(MyClient myClient) {
        System.out.println("Threads: " + myClient.getThreadCount());
    }

    private void printIterationsNumber(MyClient myClient) {
        System.out.println("Iterations: " + myClient.getIterations());
    }
;

    private void printClientStartTime() {
        System.out.println("Client starting time: " + getStartT());
    }

    private void printThreadsEndTime() {
        System.out.println("All threads complete time: " + getEndT());
    }

    private void printRequestsSent() {
        System.out.println("Total number of requests sent: " + MyClient.Result.getRequestSent());
    }

    private void printRequestsBack() {
        System.out.println("Total number of successful responses: " + MyClient.Result.getRequestSuccess());
    }

    private void printRunTime() {
        System.out.println("run time: " + getRunTime());
    }

    private void printMeanLatency() {
        System.out.println("Mean latency: " + MyClient.Result.getMean());
    }

    private void printMedianLatency() {
        System.out.println("Median latency: " + MyClient.Result.getMedian());
    }

    private void printCalculatedPercentile(int x) {
        System.out.println(x + " percentile latency: " + MyClient.Result.calculatePercentile(x));
    }

    private void printPhaseInfo(MyClient myClient) {
        System.out.println("All threads running");
//        printClientStartTime();
//        printThreadsEndTime();
        printRunTime();
    }


    private void printToConsole(MyClient myClient) {
        printThreadNumber(myClient);
        printIterationsNumber(myClient);
        printRequestsSent();
        printRequestsBack();
        printMeanLatency();
        printMedianLatency();
        printCalculatedPercentile(99);
        printCalculatedPercentile(95);
    }

    private void setThreadAndIteration(String args[], MyClient myClientRunnable) {
        if(args.length > 1){
          inputThreads(args[0], myClientRunnable);
          inputIterations(args[1], myClientRunnable);
        }
    }

    private void inputThreads(String threadCount, MyClient myClientRunnable) {
        if(threadCount != null) myClientRunnable.setThreadCount(threadCount);
    }

    private void inputIterations(String iterations, MyClient myClientRunnable) {
        if(iterations != null) myClientRunnable.setIterations(iterations);
    }

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        for(int i = 0; i < PHASE_FACTOR.length; i++){
            System.out.println(PHASE[i] + " phase:");
            ClientMain test = new ClientMain();
            test.setStartT(System.currentTimeMillis());
            MyClient myClientRunnable = test.buildClient();

            test.setThreadAndIteration(args, myClientRunnable);
            myClientRunnable.assignWebTarget();
            test.buildThreads(myClientRunnable, (int)(PHASE_FACTOR[i] * 1.5 * Integer.parseInt(myClientRunnable.getThreadCount())));
            test.startThreads();
            test.joinThreads();

            test.setEndT(System.currentTimeMillis());
            test.setRunTime(test.calculateRunTime());

            test.printPhaseInfo(myClientRunnable);

            if(i == PHASE_FACTOR.length - 1){
                MyClient.Result.calculateMean();
                MyClient.Result.calculateMedian();
                test.printToConsole(myClientRunnable);
                long end = System.currentTimeMillis();
                System.out.println("Total run time = " + (end - start));
            }

            System.out.println("");
        }
    }
}
