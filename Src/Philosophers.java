
public class Philosophers {
    private static int NUM_PHIL = 5;

    static volatile boolean finished = false;

    static volatile boolean stop = false;

    public static void main(String[] args) throws InterruptedException {
        Object[] forks = new Object[NUM_PHIL];
        Philosopher[] phils = new Philosopher[NUM_PHIL];
        // Create forks
        for (int i = 0; i < phils.length; i++) {
            forks[i] = new Object();
        }
        // Initialize philosophers with two forks each
        for (int i = 0; i < phils.length; i++) {
            phils[i] = new Philosopher(i, forks[i], forks[(i + 1) % phils.length]);
        }
        for (Thread p : phils) {
            // TODO start the philosophers
            p.start();
        }
        // Keep running until the user presses ENTER
        new java.util.Scanner(System.in).nextLine();
        //Sets flag to true when we press enter
        stop = true;



        for (Thread p : phils) {
            // TODO request termination from all philosophers
            p.interrupt();
        }
        for (Thread p : phils) {
            // TODO wait for all philosophers to finish
            p.join();

        }
        System.out.println("All philosophers finished dining.");
    }

    static class Philosopher extends Thread {
        private final Object leftFork, rightFork;
        private final int id;


        public Philosopher(int id, Object leftFork, Object rightFork) {
            this.id = id;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
        }

        private void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is eating.");
            Thread.sleep((long) (Math.random() * 3));
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking.");
            Thread.sleep((long) (Math.random() * 3));
        }

        private void waiting() throws InterruptedException {
            System.out.println("Philosopher " + id + " is waiting.");
            Thread.sleep((long) (Math.random() * 3));
        }


        public void run() {
            try {
                //loop until enter is pressed
                while (!stop) {
                    think();
                    waiting();
                    // TODO pick up forks by synchronizing on the two forks

                    //To break symmetry, we don't apply this logic to philosopher 2 and 5
                    if (id != 1 && id != 4) {
                        synchronized (leftFork) {
                            System.out.println("Philosopher " + id + " picked up left fork.");
                            synchronized (rightFork) {
                                System.out.println("Philosopher " + id + " picked up right fork.");
                                // TODO to eat, forks have to be "held"
                                //After we enter both synchronization methods, the forks are locked and held, meaning we can start eating
                                eat();
                                // TODO put down forks by existing the synchronized context

                            }
                            //Release right fork
                            System.out.println("Philosopher " + id + " put down left fork.");
                        }
                        //Release left fork
                        System.out.println("Philosopher " + id + " put down right fork.");
                      //if this is philosopher 2 or 5
                    } else {
                        synchronized (rightFork) {
                            System.out.println("Philosopher " + id + " picked up right fork.");
                            synchronized (leftFork) {
                                System.out.println("Philosopher " + id + " picked up left fork.");
                                // TODO to eat, forks have to be "held"
                                //After we enter both synchronization methods, the forks are locked and held, meaning we can start eating
                                eat();
                                // TODO put down forks by existing the synchronized context

                            }
                            //Release left fork
                            System.out.println("Philosopher " + id + " put down left fork.");
                        }
                        //Release right fork
                        System.out.println("Philosopher " + id + " put down right fork.");
                    }
                }
            } catch (InterruptedException ie) {
                // Do nothing, interruption may cause this, but safe to ignore
            }
        }


    }
}
