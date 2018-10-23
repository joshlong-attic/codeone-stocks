package bootfx.data;

import client.StockPrice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;

import java.util.function.Consumer;

import static javafx.application.Platform.runLater;

public class SymbolData implements Consumer<StockPrice> {
	private static final int MAX_NUMBER_OF_ITEMS = 30;
	private final ObservableList<Data<String, Number>> data = FXCollections.observableArrayList();

	@Override
	public void accept(StockPrice message) {
		System.out.println("price = [" + message + "]");
		runLater(() -> addPriceToChart(message));
	}

	private void addPriceToChart(StockPrice message) {
		data.add(new Data<>(String.valueOf(message.getWhen().getSeconds()), message.getPrice()));
		if (data.size() > MAX_NUMBER_OF_ITEMS) {
			System.out.println("max exceeded");
			data.remove(0);
		}
	}

	public ObservableList<Data<String, Number>> getData() {
		return data;
	}

}

