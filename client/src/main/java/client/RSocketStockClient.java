package client;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;

@Log4j2
class RSocketStockClient implements StockClient {

	private final RSocketRequester rSocketRequester;

	RSocketStockClient(RSocketRequester.Builder builder) {
		rSocketRequester = builder.connectTcp("localhost", 7000)
								  .block();
	}

	@Override
	public Flux<StockPrice> pricesFor(String ticker) {
		return rSocketRequester.route("stockPrices")
							   .data(ticker)
							   .retrieveFlux(StockPrice.class);
	}
}
