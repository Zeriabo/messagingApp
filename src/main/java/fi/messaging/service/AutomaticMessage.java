package fi.messaging.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.messaging.pojos.Email;
public class AutomaticMessage {
	List<String> receivers = Arrays.asList("zeriab2@hotmail.com","zeriab4@hotmail.com","zeriab1@hotmail.com","zeriab@hotmail.com");
	  public static final String message = "Hello this is an automatic message";
	  Email email= new Email(100, "1", "Automatic Message", "Automatic Message", new Date(), 4,
				 receivers);

	  
	  
	  
	  public  void execute() throws IOException, InterruptedException
	  {
		 SendMessageByJava(email);
		  System.out.println("Executed");
	  }
	private void SendMessageByJava(Email m) throws IOException, InterruptedException {
		

	        var objectMapper = new ObjectMapper();
	        String requestBody = objectMapper
	                .writeValueAsString(m);

	        HttpClient client = HttpClient.newHttpClient();
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create("http://localhost:8080/sendmessage"))
	                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
	                .build();

	        HttpResponse<String> response = client.send(request,
	                HttpResponse.BodyHandlers.ofString());

	        System.out.println(response.body());
	}

}
