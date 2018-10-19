package bootfx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;

@Component
public class StockClient {

	private final WebClient webClient;

	StockClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Publisher<StockPrice> pricesFor(String ticker) {
		return this.webClient
			.get()
			.uri("http://localhost:8080/stocks/{t}", ticker)
			.retrieve()
			.bodyToFlux(StockPrice.class);
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
