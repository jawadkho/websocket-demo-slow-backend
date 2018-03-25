package uk.co.jawadkho.websockets.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class QueueService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueService.class);

    private static final int NUM_ENDPOINTS = 20;
    private static final int MAX_ATTRIBUTE_PERMITS = 5;

    private final List<Endpoint> endpoints;
    private final Random r;

    public QueueService() {
        endpoints = new ArrayList<>();

        r = new Random();

        for (int i = 0; i < NUM_ENDPOINTS; i++) {
            endpoints.add(new Endpoint("endpoint-" + i));
        }
    }

    public List<Endpoint> listEndpoints() {
        return endpoints;
    }

    private final Semaphore semaphore = new Semaphore(MAX_ATTRIBUTE_PERMITS);

    public Attributes getAttributes(Endpoint endpoint) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            return null;
        }

        try {
            Thread.sleep(r.nextInt(5000) + 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int itemsAvailable = r.nextInt(10_000);

        semaphore.release();
        return new Attributes(itemsAvailable);
    }

    public static class Endpoint {
        private String name;

        public Endpoint(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Endpoint)) return false;

            Endpoint endpoint = (Endpoint) o;

            return name != null ? name.equals(endpoint.name) : endpoint.name == null;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Endpoint{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }


    public static class Attributes {
        private int itemsAvailable;

        public Attributes(int itemsAvailable) {
            this.itemsAvailable = itemsAvailable;
        }

        public int getItemsAvailable() {
            return itemsAvailable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Attributes)) return false;

            Attributes that = (Attributes) o;

            return itemsAvailable == that.itemsAvailable;
        }

        @Override
        public int hashCode() {
            return itemsAvailable;
        }
    }
}
