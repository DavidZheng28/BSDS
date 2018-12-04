package edu.neu.client;

import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {

    String ipAddress = "localhost";
    int threadNumber = 16;
    int userNumber = 100000;
    int day = 1;
    int test = 100;

    if (args.length > 0) {
      ipAddress = args[0];
    }
    if (args.length > 1) {
      threadNumber = Integer.parseInt(args[1]);
    }
    if (args.length > 2) {
      userNumber = Integer.parseInt(args[2]);
    }
    if (args.length > 3) {
      day = Integer.parseInt(args[3]);
    }
    if (args.length > 4) {
      test = Integer.parseInt(args[4]);
    }

    String uri = "http://" + ipAddress + ":8080/webapi/myresource";

    ThreadPool threadPool = new ThreadPool(threadNumber, userNumber, day, test);
    threadPool.start(uri);
  }

}
