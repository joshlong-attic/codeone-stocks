package bootfx;

import javafx.application.Application;

/**
	* Why is this a second class? No good reason whatsoever.
	*
	* <a href="http://mail.openjdk.java.net/pipermail/openjfx-dev/2018-June/021977.html">See this
	* amazing post</a>.
	*/
public class Main {

	public static void main(String args[]) {
		Application.launch(JavaFxApplication.class, args);
	}
}
