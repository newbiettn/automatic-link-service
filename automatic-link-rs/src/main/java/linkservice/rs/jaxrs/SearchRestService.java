package linkservice.rs.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import linkservice.AutomaticLink;
import linkservice.common.UpdatePath;
import linkservice.rs.model.Query;

@Path( "/docs" ) 
public class SearchRestService {
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public String doSearch(String query ) throws Exception {
		AutomaticLink autoLink = new AutomaticLink();
		if (UpdatePath.isOutputEmpty()) {
			autoLink.initialize();
		} else {
			autoLink.checForNewDocs();
		}

		System.out.println(query);
		String json = autoLink.run(query);
		return json;
	}
}
