package c1.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.rsocket.*
import io.rsocket.transport.netty.server.NettyContextCloseable
import io.rsocket.transport.netty.server.TcpServerTransport
import io.rsocket.util.DefaultPayload
import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Stream

@SpringBootApplication
class ServiceApplication

fun main(args: Array<String>) {
	runApplication<ServiceApplication>(*args)
}

@RestController
class StockRestController(private val stockService: StockService) {

	@GetMapping(value = ["/stocks/{ticker}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun getPriceSeriesFor(@PathVariable ticker: String) =
			stockService.ensureStreamExists(ticker)
}

@Component
class StockRSocketController(
		private val objectMapper: ObjectMapper,
		private val stockService: StockService) {

	private val tcpServerTransport = TcpServerTransport.create(7000)

	@EventListener(ApplicationReadyEvent::class)
	fun serve(evt: ApplicationReadyEvent) {

		val socketAcceptor = SocketAcceptor { _, _ ->
			Mono.just<RSocket>(object : AbstractRSocket() {

				override fun requestStream(payload: Payload) =
						stockService
								.ensureStreamExists(payload.dataUtf8)
								.map { objectMapper.writeValueAsString(it) }
								.map { DefaultPayload.create(it) }

			})
		}

		RSocketFactory
				.receive()
				.acceptor(socketAcceptor)
				.transport<NettyContextCloseable>(this.tcpServerTransport)
				.start()
				.subscribe()
	}

}

@Service
class StockService {

	private val log = LogFactory.getLog(javaClass)
	private val prices = mutableMapOf<String, Flux<StockPrice>>()

	private fun randomStockPrice(ticker: String): StockPrice {
		val price = ThreadLocalRandom.current().nextDouble(1500.0)
		return StockPrice(ticker, price, Date())
	}

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