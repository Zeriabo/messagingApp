
package fi.messaging.service;




import javax.ws.rs.core.Response;


public interface StatisticsService {

	public abstract Response getStatistics() throws Exception;
	

}
