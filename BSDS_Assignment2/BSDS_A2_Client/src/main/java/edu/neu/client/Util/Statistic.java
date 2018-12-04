package edu.neu.client.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistic {

  private List<Long> latencyList;
  private List<Long> requestList;


  public Statistic(List<Long> latencyList, List<Long> requestList) {
    Collections.sort(latencyList);
    this.latencyList = latencyList;
    this.requestList = requestList;
  }

  public long getMedianLatency() {
    return latencyList.get((latencyList.size() - 1) / 2);
  }

  public long getMeanLatency() {
    long meanLatency = 0;
    for (long latency : latencyList) {
      meanLatency += latency;
    }
    return meanLatency / latencyList.size();
  }

  public long getNinetyFivePercentile() {
    return latencyList.get((latencyList.size() - 1) * 99 / 100);
  }

  public long getNinetyNinePercentile() {
    return latencyList.get((latencyList.size() - 1) * 99 / 100);
  }

  public void writeToCSV() {
    Map<Long, Integer> requestMap = new HashMap<>();
    for (Long requestTime : requestList) {
      requestMap.put(requestTime, requestMap.getOrDefault(requestTime, 0) + 1);
    }
    List<Long> keyList = new ArrayList<>(requestMap.keySet());
    Collections.sort(keyList);

    FileWriter fileWriter = null;

    try {
      fileWriter = new FileWriter("throughput.csv");
      fileWriter.append("time, frequency\n");
      for (long key : keyList) {
        int timeDiff = (int) (key - keyList.get(0));
        fileWriter.append(String.valueOf(timeDiff));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(requestMap.get(key)));
        fileWriter.append("\n");
      }
    } catch (Exception e) {
      System.out.println("Cannot write to csv!");
      e.printStackTrace();
    } finally {
      try {
        fileWriter.close();
      } catch (IOException e) {
        System.out.println("Cannot close csv!");
        e.printStackTrace();
      }
    }
  }

}
