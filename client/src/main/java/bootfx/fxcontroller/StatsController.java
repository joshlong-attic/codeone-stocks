package bootfx.fxcontroller;

import bootfx.StockClient;
import bootfx.data.StockInfoItem;
import bootfx.data.StockStats;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

@Component
public class StatsController {
    @FXML
    public TableView<StockInfoItem> stats;
    private StockClient stockClient;
    private String symbol = "GOOG";

    public StatsController(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    @FXML
    public void initialize() {
        StockStats stockStats = new StockStats();
        stats.setItems(stockStats.getData());
        this.stockClient.pricesFor(symbol).subscribe(stockStats);
    }
}
