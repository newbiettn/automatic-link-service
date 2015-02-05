package linkservice.index;

import linkservice.clustering.Bar;
import linkservice.clustering.Foo;

public class SingletonDemo {
	private SingletonDemo () {}
	
	private Foo foo;
	private Bar bar;
	
	private static class SingletonDemoHolder {
		private static final SingletonDemo INSTANCE = new SingletonDemo();
	}
	public static synchronized SingletonDemo getInstance(){
		return SingletonDemoHolder.INSTANCE;
	}
	public void init(Foo f, Bar b) {
		this.foo = f;
		this.bar = b;
	}
}
