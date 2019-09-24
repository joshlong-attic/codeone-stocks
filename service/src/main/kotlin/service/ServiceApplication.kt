package service

import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Stream

@SpringBootApplication
class ServiceApplication

fun main(args: Array<String>) {
	runApplication<ServiceApplication>(*args)
}

@RestController
class StockRestController(private val stockService: StockService) {

	@GetMapping(value = ["/stocks/{ticker}"],
			produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun getPriceSeriesFor(@PathVariable ticker: String) =
			stockService.ensureStreamExists(ticker)
}

@Controller
class StockPricesRSocketController(private val stockService: StockService) {

	@MessageMapping("stockPrices")
	fun prices(symbol: String) = stockService.ensureStreamExists(symbol)
}

@Service
class StockService {

	private val log = LogFactory.getLog(javaClass)
	private val prices = ConcurrentHashMap<String, Flux<StockPrice>>()

	private fun randomStockPrice(ticker: String) =
		StockPrice(ticker, ThreadLocalRandom.current().nextDouble(1500.0), Date())

	fun ensureStreamExists(ticker: String): Flux<StockPrice> = this.prices
			.computeIfAbsent(ticker) { _ ->
				Flux
						.fromStream(Stream.generate<StockPrice> { this.randomStockPrice(ticker) })
						.delayElements(Duration.ofSeconds(1))
						.share()
						.doOnSubscribe { _ -> log.info("new subscription for ticker " + ticker + '.'.toString()) }
						.doOnCancel { this.prices.remove(ticker) }
			}
}

class StockPrice(
		val ticker: String,
		val price: Double,
		val `when`: Date)