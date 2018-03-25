package uk.co.jawadkho.websockets.demo.service;

public class Queue {
    private final QueueService.Endpoint endpoint;
    private final QueueService.Attributes attributes;

    public Queue(QueueService.Endpoint endpoint, QueueService.Attributes attributes) {
        this.endpoint = endpoint;
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Queue)) return false;

        Queue queue = (Queue) o;

        if (endpoint != null ? !endpoint.equals(queue.endpoint) : queue.endpoint != null) return false;
        return attributes != null ? attributes.equals(queue.attributes) : queue.attributes == null;
    }

    @Override
    public int hashCode() {
        int result = endpoint != null ? endpoint.hashCode() : 0;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Queue{" +
                "endpoint=" + endpoint +
                ", attributes=" + attributes +
                '}';
    }
}
