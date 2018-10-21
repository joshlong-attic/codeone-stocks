package com.example.pump;

import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@SpringBootApplication
public class PumpApplication {

	public static void main(String[] args) {
		SpringApplication.run(PumpApplication.class, args);
		new Scanner(System.in).next();
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
class StockPrice {
	@Id
	private String id;
	private String ticker;
	private double price;
	private Date when;
}

interface StockPriceRepository extends ReactiveMongoRepository<StockPrice, String> {
}

@Log4j2
@Component
class Pump {

	private final Map<String, Flux<StockPrice>> prices = new ConcurrentHashMap<>();
	private final StockPriceRepository stockPriceRepository;
	private final ReactiveMongoTemplate operations;

	Pump(StockPriceRepository stockPriceRepository, ReactiveMongoTemplate operations) {
		this.stockPriceRepository = stockPriceRepository;
		this.operations = operations;
	}

	private Mono<MongoCollection<org.bson.Document>> install() throws Exception {

		Class<StockPrice> stockPriceClass = StockPrice.class;

		CollectionOptions capped =
			CollectionOptions
				.empty()
				.size(1024 * 1024)
				.maxDocuments(100)
				.capped();

		return operations
			.collectionExists(stockPriceClass)
			.flatMap(exists -> exists ? operations.dropCollection(stockPriceClass)
				: Mono.just(exists))
			.then(operations.createCollection(stockPriceClass, capped));
	}

	@EventListener(ApplicationReadyEvent.class)
	public void pump(ApplicationReadyEvent event) throws Exception {

		final Mono<MongoCollection<org.bson.Document>> install = install();
		final Flux<StockPrice> priceFlux = Flux
			.just("ADOB", "AWS", "GOOG", "IBM", "MSFT", "ORCL", "PVTL", "RHT")
			.map(this::ensureStreamExists)
			.flatMap(sps -> sps.flatMap(this.stockPriceRepository::save));

		install
			.thenMany(priceFlux)
			.subscribe(log::info);
	}

	private Flux<StockPrice> ensureStreamExists(String ticker) {
		return this.prices
			.computeIfAbsent(ticker, stock -> Flux
				.fromStream(Stream.generate(() -> this.randomStockPrice(ticker)))
				.delayElements(Duration.ofSeconds(1))
				.share()
				.doOnSubscribe(subscription -> log.info("new subscription for ticker " + ticker + '.'))
				.doOnCancel(() -> this.prices.remove(ticker)));
	}

	private StockPrice randomStockPrice(String ticker) {
		double price = ThreadLocalRandom.current().nextDouble(1500);
		return new StockPrice(null, ticker, price, new Date());
	}
}
