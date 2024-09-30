package com.umar.notificationservice.inventoryservice;

import com.umar.notificationservice.inventoryservice.model.Inventory;
import com.umar.notificationservice.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository)
	{
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setQuantity(1);
			inventory.setSkuCode("Iphone-14");

			Inventory inventory1 = new Inventory();
			inventory1.setQuantity(0);
			inventory1.setSkuCode("Iphone-15");

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory1);
		};
	}
}
