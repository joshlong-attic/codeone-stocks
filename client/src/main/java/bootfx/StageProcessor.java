package bootfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
class StageProcessor implements ApplicationListener<StageReadyEvent> {

	private final ApplicationContext applicationContext;

	StageProcessor(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
		try {
			Stage stage = stageReadyEvent.getStage();
			ClassPathResource fxml = new ClassPathResource("/ui.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxml.getURL());
			fxmlLoader.setControllerFactory(this.applicationContext::getBean);
			Parent root = fxmlLoader.load();
			drawStockScreen(root);
			Scene scene = new Scene(root, 800, 600);
			stage.setScene(scene);
			stage.show();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void drawStockScreen(Parent parent) {

	}
}
