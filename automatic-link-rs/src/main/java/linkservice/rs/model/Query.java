package linkservice.rs.model;

public class Query {

	public String content;
	
	public String name;
	
	public Query() {
		
	}
	
	public Query(String content, String name) {
        this.name = name;
        this.content = content;
    }
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
