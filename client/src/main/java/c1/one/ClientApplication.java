package c1.one;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaFxApplication.class, args);
	}

/*	private ConfigurableApplicationContext context;


	@Override
	public void init() throws Exception {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(AirQualityFxApplication.class);
		context = builder.run(getParameters().getRaw().toArray(new String[0]));

		FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
		loader.setControllerFactory(context::getBean);
		rootNode = loader.load();
	}

	@Override
	public void start(Stage stage) throws Exception {

	}*/
}

/*
class SymbolData implements Consumer<String> {
	private final ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();
	private long tick = 0;

	@Override
	public void accept(String price) {
		System.out.println("price = [" + price + "]");
		runLater(() -> data.add(new XYChart.Data<>(String.valueOf(tick++), Integer.valueOf(price))));
	}

	ObservableList<XYChart.Data<String, Number>> getData() {
		return data;
	}

}

class StockController {

	@FXML
	public LineChart<String, Number> stockticker;

	//currently only supports a single symbol/series on the chart
	@SuppressWarnings("unchecked")
	void setData(final SymbolData chartData) {
		final XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setData(chartData.getData());
		final ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList(series);
		stockticker.setData(seriesList);
	}
}

class StockTicker   {

	//@Override
	public void start(Stage primaryStage) throws Exception {
		// all models created in advance
		SymbolData chartData = new SymbolData();
		connect(chartData);

		// initialise the UI
		URL resource = getClass().getResource("/com/mechanitis/demo/client/stock-ticker.fxml");
		FXMLLoader loader = new FXMLLoader(resource);
		primaryStage.setTitle("Stock Prices Dashboard");
		Scene scene = new Scene(loader.load(), 900, 700);
		scene.getStylesheets().add(getClass().getResource("/com/mechanitis/demo/client/default.css").toString());

		// wire up the models to the controllers
		StockController controller = loader.getController();
		controller.setData(chartData);

		// let's go!
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void connect(SymbolData chartData) {
		WebSocketClient socketClient = new ReactorNettyWebSocketClient();

		URI uri = URI.create("ws://localhost:8083/MDB/");

		socketClient.execute(uri, (WebSocketSession session) -> {

			Mono<WebSocketMessage> out = Mono.just(session.textMessage("test"));

			Flux<String> in = session.receive()
				.map(WebSocketMessage::getPayloadAsText)
				//there's a better way to do this
				.doOnEach(incomingStockTickerString -> chartData.accept(incomingStockTickerString.get()));

			return session.send(out)
				.thenMany(in)
				.then();
		}).subscribe();
	}


	public static void main(String[] args) {
		launch();
	}

}
 */