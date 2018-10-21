package bootfx.data;

import client.StockPrice;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.function.Consumer;

public class StockStats implements Consumer<StockPrice> {

	private final ObservableList<StockInfoItem> stockInfoItems = FXCollections.observableArrayList();

	//needs to be the JavaFX property here as this is the one that's going to be watched and changed.
	private final SimpleDoubleProperty maxPrice;
	private final SimpleDoubleProperty minPrice;
	private final SimpleDoubleProperty averagePrice;
	private int numberOfPrices = 0;

	public StockStats() {
		this.maxPrice = new SimpleDoubleProperty(0);
		this.minPrice = new SimpleDoubleProperty(Double.MAX_VALUE);
		this.averagePrice = new SimpleDoubleProperty(0);
		stockInfoItems.add(new StockInfoItem("Min Price", this.minPrice));
		stockInfoItems.add(new StockInfoItem("Max Price", this.maxPrice));
		stockInfoItems.add(new StockInfoItem("Average Price", this.averagePrice));
	}

	public ObservableList<StockInfoItem> getData() {
		return stockInfoItems;
	}

	@Override
	public void accept(StockPrice stockPrice) {
		double price = stockPrice.getPrice();
		checkAndUpdateMaxPrice(price);
		checkAndUpdateMinPrice(price);
		updateAveragePrice(price);
	}

	private void checkAndUpdateMaxPrice(double price) {
		if (maxPrice.get() < price) {
			maxPrice.set(price);
		}
	}

	private void checkAndUpdateMinPrice(double price) {
		if (minPrice.get() > price) {
			minPrice.set(price);
		}
	}

	//this needs a test
	private void updateAveragePrice(double price) {
		double prevMean = averagePrice.get();
		double newMean = ((prevMean * numberOfPrices) + price) / ++numberOfPrices;
		averagePrice.set(newMean);
	}

	//TODO: also need to change the UI to limit this to 2dp

}
