package c1.one;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SimpleJavaFxController {

	SimpleJavaFxController() {
		log.info("starting the " + this.getClass().getName() + "!");
	}
}
