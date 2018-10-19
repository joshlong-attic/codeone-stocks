package c1.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@SpringBootApplication
public class ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}
}

@RestController
class StockRestController {

	private final Map<String, Flux<StockPrice>> prices = new ConcurrentHashMap<>();

	@GetMapping(value = "/stocks/{ticker}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<StockPrice> getPriceSeriesFor(@PathVariable String ticker) {
		return this.ensureStreamExists(ticker);
	}

	private Flux<StockPrice> ensureStreamExists(String ticker) {
		return this.prices
			.computeIfAbsent(ticker, stock -> Flux
				.fromStream(Stream.generate(() -> {
					double price = ThreadLocalRandom.current().nextDouble(1500);
					return new StockPrice(ticker, price, new Date());
				}))
				.delayElements(Duration.ofSeconds(1))
				.share()
			);
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