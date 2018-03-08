package dlsu.coco.coco_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class CocoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CocoApiApplication.class, args);
	}
}
