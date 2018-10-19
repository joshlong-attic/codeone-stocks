package bootfx;

import javafx.application.Application;
import javafx.application.HostServices;
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
		ApplicationContextInitializer<GenericApplicationContext> initializer =
			context -> {
				context.registerBean(Application.class, () -> JavaFxApplication.this);
				context.registerBean(Parameters.class, this::getParameters);
				context.registerBean(HostServices.class, this::getHostServices);
			};
		this.context = new SpringApplicationBuilder()
			.sources(Main.class)
			.initializers(initializer)
			.run(getParameters().getRaw().toArray(new String[0]));
	}

	@Override
	public void start(Stage stage) throws IOException {
		this.context.publishEvent(new StageReadyEvent(stage));
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		this.context.close();
	}
}

