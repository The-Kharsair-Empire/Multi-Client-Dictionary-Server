
import java.util.concurrent.LinkedBlockingDeque;


//this class the the task queue for workers in worker pool to access, it is thread safe so no any two threads in the thread pool will execute the same task
public class TaskQueue extends LinkedBlockingDeque<Task> {
    TaskQueue(){
        super();
    }

    public boolean add(Task task) {
        return super.add(task);
    }

    public Task take() throws InterruptedException{
        return super.take();
    }
}