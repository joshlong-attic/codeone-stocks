package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;

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
