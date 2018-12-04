package edu.neu.client;

import edu.neu.client.Util.Statistic;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class ThreadPool {

  private int totalRequest = 0;
  private List<Long> latencyList = new ArrayList<>();
  private List<Long> requestList = new ArrayList<>();
  private List<List<Long>> localRequestList = new ArrayList<>();
  private List<List<Long>> localLatencyList = new ArrayList<>();
  private Client client;
  private int maxThreadNum;
  private int userNumber;
  private int dayNumber;
  private int testNumber;

  private static final int PHASE_Number = 4;
  private static final String[] PHASES = {"Warmup", "Loading", "Peak", "Cooldown"};
  private static final double[] PHASE_FACTOR = {0.1, 0.5, 1, 0.25};
  private static final int[][] INTERVALS = {{0, 2}, {3, 7}, {8, 18}, {19, 23}};

  public synchronized void incrementTotalRequest() {
    totalRequest++;
  }

  public void addLatency(List<Long> localLatencyList) {
    latencyList.addAll(localLatencyList);
  }

  public void addRequest(List<Long> localRequestList) {
    requestList.addAll(localRequestList);
  }

  public ThreadPool(int maxThreadNum, int userNumber, int dayNumber, int testNumber) {
    this.maxThreadNum = maxThreadNum;
    this.userNumber = userNumber;
    this.dayNumber = dayNumber;
    this.testNumber = testNumber;
    this.client = ClientBuilder.newClient();
  }

  public void start(String url) {
    System.out.println("Deleting previous table...");
    MyClient myClient = new MyClient(url, client);
    myClient.deleteTable();

    Timestamp startTime = new Timestamp(System.currentTimeMillis());

    for (int i = 0; i < PHASE_Number; i++) {

      int intervalStart = INTERVALS[i][0];
      int intervalEnd = INTERVALS[i][1];
      int numThreads = (int) (maxThreadNum * PHASE_FACTOR[i]);
      int iterations = this.testNumber * (intervalEnd - intervalStart + 1);
      final CountDownLatch latch = new CountDownLatch(numThreads);

      Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());
      System.out.println(PHASES[i] + ":");
      System.out.println(numThreads + " threads running...............");

      localRequestList.add(new ArrayList<>());
      localLatencyList.add(new ArrayList<>());
      List<Long> rList = localRequestList.get(i);
      List<Long> lList = localLatencyList.get(i);

      try {
        for (int j = 0; j < numThreads; j++) {
          new Thread(() -> {
            int[] users = new int[3];
            int[] intervals = new int[3];
            int[] stepCounts = new int[3];
            for (int l = 0; l < 3; l++) {
              users[l] = ThreadLocalRandom.current().nextInt(1, this.userNumber + 1);
              intervals[l] = ThreadLocalRandom.current().nextInt(intervalStart, intervalEnd + 1);
              stepCounts[l] = ThreadLocalRandom.current().nextInt(1, 10000);
            }

            for (int k = 0; k < iterations; k++) {
              incrementTotalRequest();
              for (int q = 0; q < 3; q++) {
                Timestamp begin = new Timestamp(System.currentTimeMillis());
                myClient.postStepCount(users[q], dayNumber, intervals[q], stepCounts[q]);
                Timestamp end = new Timestamp(System.currentTimeMillis());
                rList.add(begin.getTime() / 1000);
                lList.add(end.getTime() - begin.getTime());
              }
            }
            latch.countDown();
          }).start();
        }
        latch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());
      System.out.println(
          "run time: " + (endTimestamp.getTime() - startTimestamp.getTime()) / 10000.0
              + " seconds");

    }

    Timestamp endTime = new Timestamp(System.currentTimeMillis());
    double RunTime = (endTime.getTime() - startTime.getTime()) / 1000.0;

    for (int i = 0; i < PHASE_Number; i++) {
      addLatency(localLatencyList.get(i));
      addRequest(localRequestList.get(i));
    }

    Statistic statistic = new Statistic(latencyList, requestList);
    statistic.writeToCSV();

    System.out.println("===========================");
    System.out.println("Total request number = " + totalRequest);
    System.out.println("Mean latency = " + statistic.getMeanLatency() / 1000.0);
    System.out.println("Median latency = " + statistic.getMedianLatency() / 1000.0);
    System.out.println("95 percentile latency = " + statistic.getNinetyFivePercentile() / 1000.0);
    System.out.println("99 percentile latency = " + statistic.getNinetyNinePercentile() / 1000.0);
    System.out.println("Total run time = " + RunTime + " seconds");


  }

}
