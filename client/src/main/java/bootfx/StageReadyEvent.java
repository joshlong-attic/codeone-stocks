package bootfx;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;


class StageReadyEvent extends ApplicationEvent {

	private final Stage stage;

	StageReadyEvent(Stage stage) {
		super(stage);
		this.stage = stage;
	}

	public Stage getStage() {
		return stage;
	}
}
