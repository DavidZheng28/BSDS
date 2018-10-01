package edu.neu.client;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MyClient implements Runnable {
    private Client client;
    private WebTarget webTarget;
    private String threadCount = "20";
    private String iterations = "100";
    private String postParam = "Hey";
    private String MYURL = "http://bsds3-env.mwfzigqfus.us-west-2.elasticbeanstalk.com/webapi/myresource";

    public String getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(String threadCount) {
        this.threadCount = threadCount;
    }

    public String getIterations() { return iterations; }

    public void setIterations(String iterations) {
        this.iterations = iterations;
    }

    public String getPostParm() {
        return postParam;
    }

    public MyClient(Client client) {
        this.client = client;
    }

    public void assignWebTarget() {
        webTarget = client.target(MYURL);
    }

    public <T> T postText(Object requestEntity, Class<T> responseType) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).post(javax.ws.rs.client.Entity.entity(requestEntity,
                javax.ws.rs.core.MediaType.TEXT_PLAIN), responseType);
    }

    public String getStatus() throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }


    @Override
    public void run() {
        for(int i = 0; i < Integer.parseInt(iterations); ++i) {
            /**
             * GET: To test GET requests individually, please comment the
             * below POST.
             */
            Result.incrementRequestSent();
            long startTimeGet = System.currentTimeMillis();
            if(getStatus().equals("Got it!")) {
                Result.incrementRequestSuccess();
                // System.out.println("GET: ok");
            }
            long endTimeGet = System.currentTimeMillis();
            Result.addLatency(endTimeGet - startTimeGet);

            /**
             * POST: To test POST requests individually, please comment the
             * above GET.
             */
            long startTimePost = System.currentTimeMillis();
            Result.incrementRequestSent();
            if(Integer.parseInt(postText(getPostParm(), String.class)) == (getPostParm().length())) {
                Result.incrementRequestSuccess();
                // System.out.println("POST: ok");
            }
            long endTimePost = System.currentTimeMillis();
            Result.addLatency(endTimePost - startTimePost);
        }
    }
    static class Result {
        private static AtomicInteger requestSent = new AtomicInteger(0);
        private static AtomicInteger requestSuccess = new AtomicInteger(0);
        private static CopyOnWriteArrayList<Long> latencyList = new CopyOnWriteArrayList<>();
        private static double mean;
        private static double median;

        public static double getMean() {
            return mean;
        }

        public static double getMedian() {
            return median;
        }

        public static double calculatePercentile(int val) {
            return latencyList.get(((latencyList.size() / 100) * val));
        }

        public static void sortMyList() {
            Object[] arr = latencyList.toArray();
            Arrays.sort(arr);
            for(int i = 0; i < latencyList.size(); ++i) {
                latencyList.set(i, (Long) arr[i]);
            }
        }

        public static void calculateMean() {

            Long sum = 0L;
            for (Long var : latencyList){
              sum += var;
            }
            mean = (double)sum / latencyList.size();
        }

        public static void calculateMedian() {
            if(latencyList == null || latencyList.size() == 0) return;
            sortMyList();
            int size = latencyList.size();
            if(size % 2 == 0) median = latencyList.get(size / 2);
            else median = (latencyList.get(size / 2) + latencyList.get((size / 2) + 1)) / 2;
        }

        public static void incrementRequestSent() {
            requestSent.addAndGet(1);
        }

        public static void incrementRequestSuccess() {
            requestSuccess.addAndGet(1);
        }

        public static int getRequestSent() {
            return requestSent.get();
        }

        public static int getRequestSuccess() {
            return requestSuccess.get();
        }

        public static void addLatency(long latency) {
            latencyList.add(latency);
        }

    }
}
