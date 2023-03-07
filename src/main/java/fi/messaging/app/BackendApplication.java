package fi.messaging.app;

import org.apache.log4j.BasicConfigurator;  
//import org.quartz.CronScheduleBuilder;  
import org.quartz.JobBuilder;  
import org.quartz.JobDetail;  
import org.quartz.Scheduler;  
import org.quartz.SimpleScheduleBuilder;  
import org.quartz.Trigger;  
import org.quartz.TriggerBuilder;  
import org.quartz.impl.StdSchedulerFactory;  
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import fi.messaging.quartz.CreateUserJob;
import fi.messaging.service.TaskExecutor;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
public class BackendApplication {
	
    private static final String NAME_OF_JOB = "CreateUser";  
    private static final String NAME_OF_GROUP = "group1";  
    private static final String NAME_OF_TRIGGER = "triggerCreateUser";  
    
    //create variable scheduler of type Scheduler  
    private static Scheduler scheduler; 
    
    public static void main(String[] args) throws Exception {
    	
    	   BasicConfigurator.configure();  
    	
    	   //initialize scheduler instance from Quartz  
           scheduler = new StdSchedulerFactory().getScheduler();  
             
           //start scheduler  
           scheduler.start();  
             
           //create scheduler trigger based on the time interval  
           Trigger triggerNew =  createTrigger();  
             
           //create scheduler trigger with a cron expression  
           //Trigger triggerNew = createCronTrigger();  
             
           //schedule trigger  
           scheduleJob(triggerNew);  
    	
    	
        URI baseUri = UriBuilder.fromUri("http://127.0.0.1/").port(8080).build();
        Server server = JettyHttpContainerFactory.createServer(baseUri, false);
      
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/*");
        ServletContainer jersey = new ServletContainer(new ResourceConfig() {{
            packages("fi.messaging.rest");
        }});
        ServletHolder holder = new ServletHolder(jersey);
        context.addServlet(holder, "/*");
        server.setHandler(context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeDataSource();
            try {
                server.stop();
            } catch (Exception e) {
                // ignored
            }
        }));
        server.start();
        
        TaskExecutor taskExecutor = new TaskExecutor();
        taskExecutor.startExecutionAt(12,0, 0);
   }
    
    //create scheduleJob() method to schedule a job  
    private static void scheduleJob(Trigger triggerNew) throws Exception {  
          
        //create an instance of the JoDetails to connect Quartz job to the CreateQuartzJob  
        JobDetail jobInstance = JobBuilder.newJob(CreateUserJob.class).withIdentity(NAME_OF_JOB, NAME_OF_GROUP).build();  
          
        //invoke scheduleJob method to connect the Quartz scheduler to the jobInstance and the triggerNew  
        scheduler.scheduleJob(jobInstance, triggerNew);  
   
    }  
    //create createTrigger() method that returns a trigger based on the time interval  
    /*private static Trigger createCronTrigger() { 
         
        //create cron expression 
        String CRON_EXPRESSION = "0 * * * * ?"; 
         
        //create a trigger to be returned from the method 
        Trigger triggerNew = TriggerBuilder.newTrigger().withIdentity(NAME_OF_TRIGGER, NAME_OF_GROUP) 
                .withSchedule(CronScheduleBuilder.cronSchedule(CRON_EXPRESSION)).build(); 
         
        //return triggerNew to schedule it in main() method 
        return triggerNew; 
    } 
    */   
    //create createTrigger() method that returns a trigger based on the time interval  
    private static Trigger createTrigger() {  
   
        //initialize time interval  
        int TIME_INTERVAL = 3;  
          
        //create a trigger to be returned from the method  
        Trigger triggerNew = TriggerBuilder.newTrigger().withIdentity(NAME_OF_TRIGGER, NAME_OF_GROUP)  
                .withSchedule(  
                        SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(TIME_INTERVAL).repeatForever())  
                .build();  
          
        // triggerNew to schedule it in main() method  
        return triggerNew;  
    }  
}
