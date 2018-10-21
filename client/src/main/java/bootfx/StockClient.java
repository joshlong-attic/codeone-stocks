package bootfx;

import bootfx.data.StockPrice;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;


public interface StockClient {
	Flux<StockPrice> pricesFor(String ticker);
}

@Component
@Log4j2
class RSocketStockClient implements StockClient {

	private final TcpClientTransport transport = TcpClientTransport.create(7000);
	private final ObjectMapper objectMapper;

	RSocketStockClient(ObjectMapper om) {
		this.objectMapper = om;
	}

	@Override
	public Flux<StockPrice> pricesFor(String ticker) {
		return RSocketFactory
			.connect()
			.transport(this.transport)
			.start()
			.flatMapMany(rSocket ->
				rSocket
					.requestStream(DefaultPayload.create(ticker))
					.map(Payload::getDataUtf8)
					.map(json -> {
						try {
							return this.objectMapper
								.readValue(json, StockPrice.class);
						}
						catch (IOException e) {
							throw new RuntimeException(e);
						}
					})
					.doFinally(signal -> rSocket.dispose())
			);
	}
}

//@Component
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

