package linkservice.common;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Define the rule to setup Logger object before tests
 * 
 * @author newbiettn
 *
 */
public class LoggerRule implements TestRule {
	private Logger logger;

	public Logger getLogger() {
		return this.logger;
	}
	
	public Statement apply(final Statement statement, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				//before test
				logger = LoggerFactory.getLogger(description.getTestClass().getName()
						+ '.' + description.getDisplayName());
				try {
					statement.evaluate();
				} finally {
					//after test
					logger = null;
				}
			}
		};
	}

}
