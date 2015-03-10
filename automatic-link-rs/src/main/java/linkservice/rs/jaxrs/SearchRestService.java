package linkservice.rs.jaxrs;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import linkservice.AutomaticLink;

@Path( "/docs" ) 
public class SearchRestService {
	@Produces( { "text/plain" } )
	@GET
	public String doSearch( @QueryParam( "page") @DefaultValue( "1" ) final int page ) throws Exception {
		AutomaticLink autoLink = new AutomaticLink();
		String json = autoLink.run("image");
		return json;
	}
}
