package bootfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
@SpringBootApplication
public class JavaFxApplication extends Application {

	private ConfigurableApplicationContext context;

	@Override
	public void init() {
		this.context = new SpringApplicationBuilder()
			.sources(JavaFxApplication.class)
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

