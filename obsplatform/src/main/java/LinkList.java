public class LinkList {

    private static class Node<T> {

        private final T data;
        private Node next;

        public Node(T data){
            this.data=data;
        }


        public void displayNode(){
            System.out.print(data+ " ");
        }

    }

    private Node first = null;
    private Node last = null;

    public boolean isEmpty() {
        return (first == null);
    }

    public <T> void addLast(T data) {
        Node n = new Node(data);
        if (isEmpty()) {
            n.next = first;
            first = n;
            last = n;
        } else {
            last.next = n;
            last = n;
            last.next = null;
        }

    }

    public void removeFirst() {

            Node temp = first;
            if (first.next == null)
                last = null;
            first = first.next;

        }


    public void displayList() {
        Node current = first;
        while (current != null) {
            current.displayNode();
            current = current.next;
        }
    }

}