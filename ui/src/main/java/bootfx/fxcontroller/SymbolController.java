package bootfx.fxcontroller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.springframework.stereotype.Component;

@Component
public class SymbolController {
    @FXML
    public ComboBox<String> symbol;
    private StatsController statsController;
    private StockController stockController;

    public SymbolController(StatsController statsController, StockController stockController) {
        this.statsController = statsController;
        this.stockController = stockController;
    }

    @FXML
    public void initialize() {
        symbol.getItems().setAll(FXCollections.observableArrayList("GOOG", "MSFT"));
        EventHandler<ActionEvent> switchSymbol =
                e -> {
                    statsController.setSymbol(symbol.getValue());
                    stockController.setSymbol(symbol.getValue());
                };
        symbol.setOnAction(switchSymbol);
        symbol.getSelectionModel().select(0);
    }

}
