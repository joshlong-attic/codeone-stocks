package bootfx;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class StockController {

	@FXML
	LineChart<String, Number> stockticker;

	public StockController() {
		log.info("starting " + StockController.class.getName() + ".");
	}

	@FXML
	public void initialize() {
		log.info("calling FXML initialize()");
	}
}
