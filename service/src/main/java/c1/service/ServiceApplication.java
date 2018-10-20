package c1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

@Log4j2
@Service
class StockService {

	private final Map<String, Flux<StockPrice>> prices = new ConcurrentHashMap<>();

	private StockPrice randomStockPrice(String ticker) {
		double price = ThreadLocalRandom.current().nextDouble(1500);
		return new StockPrice(ticker, price, new Date());
	}

	Flux<StockPrice> ensureStreamExists(String ticker) {
		return this.prices
			.computeIfAbsent(ticker, stock -> Flux
				.fromStream(Stream.generate(() -> this.randomStockPrice(ticker)))
				.delayElements(Duration.ofSeconds(1))
				.share()
				.doOnSubscribe(subscription -> log.info("new subscription for ticker " + ticker + '.'))
				.doOnCancel(() -> this.prices.remove(ticker)));
	}
}

@Component
class StockRSocketController {

	private final TcpServerTransport tcpServerTransport = TcpServerTransport.create(7000);
	private final ObjectMapper objectMapper;
	private final StockService stockService;

	StockRSocketController(ObjectMapper objectMapper, StockService stockService) {
		this.objectMapper = objectMapper;
		this.stockService = stockService;
	}

	private String json(StockPrice sp) {
		try {
			return this.objectMapper
				.writeValueAsString(sp);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void serve(ApplicationReadyEvent evt) {

		SocketAcceptor socketAcceptor = (setupPayload, rSocket) ->
			Mono.just(new AbstractRSocket() {

				@Override
				public Flux<Payload> requestStream(Payload payload) {
					return stockService
						.ensureStreamExists(payload.getDataUtf8())
						.map(sp -> json(sp))
						.map(DefaultPayload::create);
				}
			});

		RSocketFactory
			.receive()
			.acceptor(socketAcceptor)
			.transport(this.tcpServerTransport)
			.start()
			.subscribe();
	}

}

@Log4j2
@RestController
class StockRestController {

	private final StockService stockService;

	StockRestController(StockService stockService) {
		this.stockService = stockService;
	}

	@GetMapping(value = "/stocks/{ticker}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<StockPrice> getPriceSeriesFor(@PathVariable String ticker) {
		return stockService.ensureStreamExists(ticker);
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