package c1.one;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Component
public class JavaFxApplication extends Application
	implements ApplicationListener<ApplicationReadyEvent>,
	ApplicationContextAware {

	private final AtomicReference<ApplicationContext> applicationContext =
		new AtomicReference<>();

	@Override
	public void start(Stage stage) throws IOException {
		ClassPathResource uiFxml = new ClassPathResource("/ui.fxml");
		Assert.isTrue(uiFxml.exists(), "the FXML must exist");
		FXMLLoader loader = new FXMLLoader(uiFxml.getURL());
		loader.setControllerFactory(clzz -> {
			log.info("class: " + clzz.getName() + ".");
			if (this.applicationContext.get() == null) {
				log.info("the applicationContext is null");
				return null;
			}
			else {
				return this.applicationContext.get().getBean(clzz);
			}
		});
		loader.load();
		log.info("#starting(stage)");

		stage.setTitle("Hello, world!");

		Label label = new Label();
		label.setText("Hello, world!");
		label.setTextFill(Color.BLACK);

		stage.setScene(new Scene(label, 250, 250));
		stage.show();
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent evt) {
		log.info("#launch()");
		launch();
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
		throws BeansException {
		this.applicationContext.set(ctx);
	}
}
