package bootfx;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;

@Log4j2
public class JavaFxApplication extends Application {

	private ConfigurableApplicationContext context;

	@Override
	public void init() {
		this.context = new SpringApplicationBuilder()
			.sources(Main.class)
			.run();
	}

	@Override
	public void start(Stage stage) {
		this.context.publishEvent(new StageReadyEvent(stage));
	}

	@Override
	public void stop() {
		this.context.close();
		Platform.exit();
	}
}

