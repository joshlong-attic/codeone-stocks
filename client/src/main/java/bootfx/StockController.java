package bootfx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class StockController {

	@FXML
	LineChart<String, Number> stockticker;

	private final StockClient stockClient;

	private final Collection<String> stocks = Arrays.asList(
		"ADOB", "AWS", "GOOG", "IBM", "MSFT", "ORCL", "PVTL", "RHT");

	private final Map<String, XYChart.Series<String, Number>> data =
		stocks
			.stream()
			.collect(Collectors.toMap(k -> k, k -> new XYChart.Series<>(FXCollections.observableArrayList())));

	StockController(StockClient stockClient) {
		this.stockClient = stockClient;
	}

	private void updateChart() {
		var values =
			new ArrayList<XYChart.Series<String, Number>>(data.values());
		stockticker.setData(FXCollections.observableList(values));
	}

	private void contributeNewPriceToTicker(StockPrice ticker) {
		Platform.runLater(() -> {
			var dataPoint = new XYChart.Data<String, Number>(ticker.getTicker(),
				ticker.getPrice());
			this.data.get(ticker.getTicker()).getData().add(dataPoint);
			this.updateChart();
		});
	}

	@FXML
	public void initialize() {
		var publisherMap =
			this.stocks.stream().collect(Collectors.toMap(k -> k,
				this.stockClient::pricesFor));
		publisherMap.values().forEach(p -> p.subscribe(this::contributeNewPriceToTicker));
	}
}
