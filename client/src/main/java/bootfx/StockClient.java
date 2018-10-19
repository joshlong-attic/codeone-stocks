package bootfx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Date;

@Component
@Log4j2
public class StockClient {

	private final WebClient webClient;

	StockClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Flux<StockPrice> pricesFor(String ticker) {
		return this.webClient
			.get()
			.uri("http://localhost:8080/stocks/{t}", ticker)
			.retrieve()
			.bodyToFlux(StockPrice.class)
			.doOnError(IOException.class,
				ioEx -> log.info("closing stream for " + ticker + '.'));
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class StockPrice {
	private String ticker;
	private double price;
	private Date when;
}
