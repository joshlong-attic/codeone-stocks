package client;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;


@Log4j2
class WebClientStockClient implements StockClient {

	private final WebClient webClient;

	WebClientStockClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Flux<StockPrice> pricesFor(String ticker) {
		return this.webClient
			.get()
			.uri("http://localhost:8080/stocks/{t}", ticker)
			.retrieve()
			.bodyToFlux(StockPrice.class)
			.retryBackoff(10, Duration.ofSeconds(1), Duration.ofSeconds(30))
			.doOnError(IOException.class, ioEx -> log.info("closing stream for " + ticker + '.'));
	}
}
