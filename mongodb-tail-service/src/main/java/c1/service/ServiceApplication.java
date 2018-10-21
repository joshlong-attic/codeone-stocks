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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@SpringBootApplication
public class ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}
}

interface StockPriceRepository extends ReactiveMongoRepository<StockPrice, String> {

	@Tailable
	Flux<StockPrice> findByTicker(String t);
}

@Log4j2
@Service
class StockService {

	private final StockPriceRepository stockPriceRepository;

	StockService(StockPriceRepository stockPriceRepository) {
		this.stockPriceRepository = stockPriceRepository;
	}

	Flux<StockPrice> ensureStreamExists(String ticker) {
		return this.stockPriceRepository
			.findByTicker(ticker);
	}
}

@Component
class StockRSocketController {

	private final TcpServerTransport tcpServerTransport = TcpServerTransport.create(7000);
	private final ObjectMapper objectMapper;
	private final StockPriceRepository repository;

	StockRSocketController(ObjectMapper objectMapper, StockPriceRepository repository) {
		this.objectMapper = objectMapper;
		this.repository = repository;
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
					return repository.findByTicker(payload.getDataUtf8())
						.map(sp -> json(sp))
						.map(DefaultPayload::create)
						.doFinally(signal -> dispose());
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

	private final StockPriceRepository stockPriceRepository;

	StockRestController(StockPriceRepository stockPriceRepository) {
		this.stockPriceRepository = stockPriceRepository;
	}

	@GetMapping(value = "/stocks/{ticker}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<StockPrice> getPriceSeriesFor(@PathVariable String ticker) {
		return this.stockPriceRepository.findByTicker(ticker);
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class StockPrice {
	@Id
	private String id;
	private String ticker;
	private double price;
	private Date when;
}