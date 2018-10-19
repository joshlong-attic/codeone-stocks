package bootfx;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Log4j2
@Component
class StageListener {

	@EventListener(StageReadyEvent.class)
	public void stageIsReady(StageReadyEvent event) {

		log.info("stage is ready.");

		Stage stage = event.getStage();
		stage.setTitle("Hello World!");
		Button btn = new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(event1 -> log.info("Hello World!"));

		StackPane root = new StackPane();
		root.getChildren().add(btn);
		stage.setScene(new Scene(root, 300, 250));
		stage.show();

	}
}
