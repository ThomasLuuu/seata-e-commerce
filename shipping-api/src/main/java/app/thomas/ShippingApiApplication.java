package app.thomas;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ShippingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShippingApiApplication.class, args);
	}

	@Bean
	public OpenTelemetry openTelemetry() {
		return OpenTelemetrySdk.builder().build();
	}

	@EventListener(ContextClosedEvent.class)
	public void onContextClosed(ContextClosedEvent event) {
		if (event.getApplicationContext().getBean(OpenTelemetry.class) instanceof OpenTelemetrySdk) {
			((OpenTelemetrySdk) event.getApplicationContext().getBean(OpenTelemetry.class)).close();
		}
	}
}