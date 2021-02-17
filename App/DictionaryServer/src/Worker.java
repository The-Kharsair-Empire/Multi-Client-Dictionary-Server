
public class Worker extends Thread{
    private TaskQueue taskQueue;
    private boolean isHandlingTask = true;

    public Worker(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void run(){ //each worker will stare at the taskqueue for any new task received by the task receiver
        System.out.println("worker start running");

        while (isHandlingTask) {
            try {
                System.out.println("taking new task");
                taskQueue.take().run();
                System.out.println("A client has disconnected");
            }catch (InterruptedException ite) {
                System.out.println("other thread is trying to interrupt");

            }
        }


    }

    public void stopHandlingTask() {
        isHandlingTask = false;
    }
}
