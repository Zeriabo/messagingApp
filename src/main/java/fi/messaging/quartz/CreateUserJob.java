package fi.messaging.quartz;

import java.util.ServiceLoader;

//import classes  
import org.apache.log4j.Logger;  
import org.quartz.Job;  
import org.quartz.JobExecutionContext;  
import org.quartz.JobExecutionException;

import fi.messaging.service.UserService;  

public class CreateUserJob  implements Job{

	//Create instance of logger  
    private Logger log = Logger.getLogger(CreateUserJob.class);  
    
    
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	
		UserService userService= getUserService();
		//check the count of the records in user_requests table with date now or less than now
		
		try {
			int count = userService.checkRequestsCounts();
			if(count>0)
			{
				//start creating users
				userService.createRequestedContracts();
			}
		} catch (Exception e) {
	
			throw new JobExecutionException(e.getMessage());
		}
		
		
		// if there is count 
		
		// go and create the contracts and activate them 
	}
	
	public static UserService getUserService() {
	     // load our plugin
     ServiceLoader<UserService> serviceLoader =ServiceLoader.load(UserService.class);
     for (UserService provider : serviceLoader) {
         return provider;
     }
     throw new NoClassDefFoundError("Unable to load a driver "+UserService.class.getName());
	}

}
