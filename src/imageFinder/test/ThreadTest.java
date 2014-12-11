package imageFinder.test;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 2014年12月7日
 * @author decaywood
 *
 */
public class ThreadTest {

    public static void main(String[] args) {
        
        System.out.println(Runtime.getRuntime().availableProcessors());
        
//        ExecutorService threadPool = Executors.newFixedThreadPool(3);
//        ArrayList<Runnable> runnables = new ArrayList<>();
//        runnables.add(new Runnable() {
//            
//            @Override
//            public void run() {
//                while(true)
//                System.out.println("currentThread: " + Thread.currentThread() + " is running");
//            }
//        });
//        runnables.add(new Runnable() {
//            
//            @Override
//            public void run() {
//                while(true)
//                System.out.println("currentThread: " + Thread.currentThread() + " is running");
//            }
//        });
//        runnables.add(new Runnable() {
//     
//            @Override
//            public void run() {
//                while(true)
//                System.out.println("currentThread: " + Thread.currentThread() + " is running");
//            }
//        });
//        
//        for(Runnable runnable : runnables)
//            threadPool.execute(runnable);
    }
}
