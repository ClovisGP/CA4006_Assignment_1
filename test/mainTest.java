package test;
import test.testThread;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

public class mainTest {
    public static void main(String[] args) {
      ArrayList<testThread> tesList = new ArrayList<testThread>();
      tesList.add(new testThread());
      tesList.add(new testThread());
      timeScheduler scheduler = new timeScheduler(tesList);
      scheduler.start();

    }
  }
    
    // try {
    //   //TimeUnit.SECONDS.sleep(1);
    // } catch (InterruptedException e) {
    //   System.out.println(e);
    // }