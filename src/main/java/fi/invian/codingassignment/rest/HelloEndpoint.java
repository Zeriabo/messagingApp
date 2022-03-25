package fi.invian.codingassignment.rest;

import fi.invian.codingassignment.app.DatabaseConnection;
import fi.invian.codingassignment.pojos.HelloPojo;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HelloEndpoint {
    @GET
    public HelloPojo getMessage() throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            try (PreparedStatement p = c.prepareStatement("SELECT messagebody FROM messages")) {
                ResultSet r = p.executeQuery();
                if (r.next()) {
                    return new HelloPojo(r.getString(1));
                } else {
                    throw new NotFoundException("Database did not contain the expected message.");
                }
            }
        }
    }
}
