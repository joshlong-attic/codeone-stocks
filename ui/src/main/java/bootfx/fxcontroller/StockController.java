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

import static javafx.collections.FXCollections.observableArrayList;

@Log4j2
@Component
public class StockController {

	@FXML
	private LineChart<String, Number> chart;

	private String symbol = "GOOG";

	private final StockClient stockClient;


	StockController(StockClient stockClient) {
		this.stockClient = stockClient;
	}

	@FXML
	public void initialize() {
		SymbolData symbolData = new SymbolData();
		final XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setData(symbolData.getData());
		chart.setData(observableArrayList(series));
		this.chart.setTitle(symbol);
		this.stockClient.pricesFor(symbol).subscribe(symbolData);
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
		initialize();
	}
}
