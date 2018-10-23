package bootfx.fxcontroller;

import bootfx.data.SymbolData;
import client.StockClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class StockController {

	@FXML
	LineChart<String, Number> chart;

	//TODO: this needs to be set by something external, like a dropdown or something
	private final String symbol = "GOOG";

	private final StockClient stockClient;
	private final SymbolData symbolData = new SymbolData();

	StockController(StockClient stockClient) {
		this.stockClient = stockClient;
	}

	@FXML
	public void initialize() {
		final XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setData(symbolData.getData());
		final ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList(series);
		chart.setData(seriesList);
		this.chart.setTitle(symbol);
		this.stockClient.pricesFor(symbol).subscribe(symbolData);
	}
}
