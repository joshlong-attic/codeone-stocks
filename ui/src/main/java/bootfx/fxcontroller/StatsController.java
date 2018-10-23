package bootfx.fxcontroller;

import bootfx.data.StockInfoItem;
import bootfx.data.StockStats;
import client.StockClient;
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

	public void setSymbol(String symbol) {
		this.symbol = symbol;
		//need to stop the old feed? and subscribe to the new one

		//reset UI and resubscribe
		initialize();
	}
}
