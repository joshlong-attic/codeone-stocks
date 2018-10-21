package bootfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
class StageInitializer implements ApplicationListener<StageReadyEvent> {

	private final String applicationTitle;
	private final ApplicationContext applicationContext;

	StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
																		ApplicationContext applicationContext) {
		this.applicationTitle = applicationTitle;
		this.applicationContext = applicationContext;
	}

	@Override
	public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
		try {
			Stage stage = stageReadyEvent.getStage();
			ClassPathResource fxml = new ClassPathResource("/dashboard.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxml.getURL());
			fxmlLoader.setControllerFactory(this.applicationContext::getBean);
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 800, 600);
			scene.getStylesheets().add(getClass().getResource("/default.css").toString());
			stage.setScene(scene);
			stage.setTitle(this.applicationTitle);
			stage.show();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
