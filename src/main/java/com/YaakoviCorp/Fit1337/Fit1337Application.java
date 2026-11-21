package com.YaakoviCorp.Fit1337;

import com.YaakoviCorp.Fit1337.config.RedisDetailsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(value = { RedisDetailsConfig.class })
public class Fit1337Application {

	public static void main(String[] args) {
		try {
			SpringApplication.run(Fit1337Application.class, args);
		} catch (Throwable t) {
			System.err.println("=== CRITICAL LAUNCH ERROR DETECTED ===");
			t.printStackTrace();
		}
	}

}
