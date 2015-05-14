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
	public String doSearch(Query query ) throws Exception {
		AutomaticLink autoLink = new AutomaticLink();
		if (UpdatePath.isOutputEmpty()) {
			autoLink.initialize();
		} else {
			autoLink.checForNewDocs();
		}
		
		String queryStr = "";
		if (query.getContent().length() > 0) {
			queryStr = "contents_no_filtering:" + query.getContent();
		}
		System.out.println(queryStr);
		if (query.getTitle().length() > 0) {
			queryStr += " filename:" + query.getTitle();
		}
		System.out.println(queryStr);
//		if (query.getAuthor().length() > 0) {
//			queryStr += "author:" + query.getAuthor();
//		}
		System.out.println(queryStr);
		String json = autoLink.run(queryStr);
		return json;
	}
}
