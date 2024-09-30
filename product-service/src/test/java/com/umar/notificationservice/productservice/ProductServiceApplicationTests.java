package com.umar.notificationservice.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umar.notificationservice.productservice.DTO.ProductRequest;
import com.umar.notificationservice.productservice.Repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//Test containers allow you to create lightweight , isolated instances(of docker) of
//dependencies like DBs,message queues or other services that you r applications
//uses.

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})

class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer
			("mongo:4.4.2");
	@Autowired
	private  MockMvc mockMvc;

	@Autowired
    ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry)
	{
		dynamicPropertyRegistry.add
				("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}
	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest request  = getProductRequest();
		String requestJson = new ObjectMapper().writeValueAsString(request);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(requestJson))).andExpect(status().isAccepted());

		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder().name("T-shirt")
				.description("A black cotton t-shirt")
				.price(BigDecimal.valueOf(599)).build();
	}

}
