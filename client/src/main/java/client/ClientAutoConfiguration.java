package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientAutoConfiguration {

	@Bean
	@Profile(ClientProfiles.RSOCKET)
	StockClient rSocketStockClient(ObjectMapper objectMapper) {
		return new RSocketStockClient(objectMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	WebClient webClient() {
		return WebClient.builder().build();
	}

	@Bean
	@Profile(ClientProfiles.SSE)
	StockClient webClientStockClient(WebClient client) {
		return new WebClientStockClient(client);
	}

}
