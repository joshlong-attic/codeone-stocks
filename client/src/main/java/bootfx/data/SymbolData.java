package bootfx.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;

import java.util.function.Consumer;

import static javafx.application.Platform.runLater;

public class SymbolData implements Consumer<StockPrice> {
    private static final int MAX_NUMBER_OF_ITEMS = 30;
    private final ObservableList<Data<String, Number>> data = FXCollections.observableArrayList();
//    private final StockStats stockStats;
    //need a better value for x axis
    private long tick = 0;

//    public SymbolData(StockStats stockStats) {
//        this.stockStats = stockStats;
//    }

    @Override
    public void accept(StockPrice message) {
        System.out.println("price = [" + message + "]");
        double price = message.getPrice();
//        stockStats.update(price);
        runLater(() -> addPriceToChart(price));
    }

    private void addPriceToChart(Double price) {
        data.add(new Data<>(String.valueOf(tick++), price));
        if (data.size() > MAX_NUMBER_OF_ITEMS) {
            data.remove(0);
        }
    }

    public ObservableList<Data<String, Number>> getData() {
        return data;
    }

}

