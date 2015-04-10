package linkservice.rs.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class Query {

	public String content;
	
	public String title;
	
	public String author;
	
	public Query(@JsonProperty("content") String content, 
			@JsonProperty("title") String title, 
			@JsonProperty("author") String author) {
		this.content = content;
		this.title = title;
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setTitle(String title) {
		this.title = title;
	}	
}
