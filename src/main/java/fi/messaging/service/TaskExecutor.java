package fi.messaging.service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskExecutor  {
	
	    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	   
	    volatile boolean isStopIssued;

		public void startExecutionAt(int targetHour, int targetMin, int targetSec)
	    {
	        Runnable taskWrapper = new Runnable(){

	            @Override
	            public void run() 
	            {
	            	AutomaticMessage automaticMessage = new AutomaticMessage();
	            	try {
						automaticMessage.execute();
					} catch (IOException | InterruptedException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
	            	System.out.println("Automatic task Executed");
	            }
	            
	        };
	        long delay = computeNextDelay(targetHour, targetMin, targetSec);
	        executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
	    }

	    private long computeNextDelay(int targetHour, int targetMin, int targetSec) 
	    {
	        LocalDateTime localNow = LocalDateTime.now();
	        ZoneId currentZone = ZoneId.systemDefault();
	        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
	        ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
	        if(zonedNow.compareTo(zonedNextTarget) > 0)
	            zonedNextTarget = zonedNextTarget.plusDays(1);
	        
	        Duration duration = Duration.between(zonedNow, zonedNextTarget);
	        return duration.getSeconds();
	    }
	    
	    public void stop()
	    {
	        executorService.shutdown();
	        try {
	            executorService.awaitTermination(1, TimeUnit.DAYS);
	        } catch (InterruptedException ex) {
	            System.out.println(ex.getMessage());
	        }
	    }

		
}
