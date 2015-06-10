public class LinkListqueueDemo {

    public static void main(String[] args) {

        LinkListQueue queueImpl = new LinkListQueue();

        queueImpl.enqueue("A");
        queueImpl.enqueue("B");
        queueImpl.enqueue("C");
        queueImpl.enqueue("D");
        queueImpl.displayQueue();
        queueImpl.dequeue();
        queueImpl.displayQueue();

    }

}