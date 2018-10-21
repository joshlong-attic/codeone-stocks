package recorder

import client.StockClient
import client.StockPrice
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component
import java.util.*

@SpringBootApplication
class RecorderApplication

fun main(args: Array<String>) {
	runApplication<RecorderApplication>(*args)
	Scanner(System.`in`).next()
}

interface StockPriceRepository : ReactiveMongoRepository<StockPrice, String> {}

@Component
class Recorder(
		private val client: StockClient,
		private val spr: StockPriceRepository) {

	@EventListener(ApplicationReadyEvent::class)
	fun ready(are: ApplicationReadyEvent) {

		this.client
				.pricesFor("GOOG")
				.doOnNext { println("recording ${it.ticker} @ ${it.price} ") }
				.subscribe { spr.save(it) }


	}
}