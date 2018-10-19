package bootfx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Component
public class StockController {


	@FXML
	LineChart<String, Number> stockticker;

	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	private final Collection<String> stocks = Arrays.asList(
		"ORCL", "PVTL", "MSFT", "GOOG", "AWS");

	private final Map<String, XYChart.Series<String, Number>> data =
		stocks
			.stream()
			.collect(Collectors.toMap(k -> k, k -> new XYChart.Series<>(FXCollections.observableArrayList())));
	private final Runnable runnable = () -> {
		List<XYChart.Series<String, Number>> values =
			new ArrayList<>(data.values());
		Platform.runLater(() -> stockticker.setData(FXCollections.observableList(values)));
		stocks.forEach(this::contributeNewPriceToTicker);
	};

	private void contributeNewPriceToTicker(String ticker) {
		Runnable run = () -> {
			var price = ThreadLocalRandom.current().nextDouble(1000);
			var dataPoint = new XYChart.Data<String, Number>(ticker, price);
			this.data.get(ticker).getData().add(dataPoint);
		};
		Platform.runLater(run);
	}

	@FXML
	public void initialize() {
		this.executor.scheduleAtFixedRate(this.runnable, 0, 1000,
			TimeUnit.MILLISECONDS);
	}
}
