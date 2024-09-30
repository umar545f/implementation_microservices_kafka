package com.umar.orderservice.service;

import com.umar.orderservice.dto.OrderLineItemsDto;
import com.umar.orderservice.dto.OrderRequest;
import com.umar.orderservice.model.Order;
import com.umar.orderservice.model.OrderLineItem;
import com.umar.orderservice.repository.OrderRepository;
import com.umar.orderservice.dto.InventoryResponse;
import com.umar.orderservice.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClient;

    @Autowired
    private  KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;


    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItemList = orderRequest
                .getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        order.setOrderLineItemList(orderLineItemList);

        List<String> skuCodes = order.getOrderLineItemList().stream()
                .map(OrderLineItem::getSkuCode)
                .collect(Collectors.toList());

        log.info("SKU Codes: {}", skuCodes);

        InventoryResponse[] responses;
        try {
            responses = webClient.build().get()
                    .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .doOnError(e -> log.error("Error fetching inventory: ", e))
                    .block();
        } catch (Exception e) {
            log.error("Exception during inventory service call: ", e);
            return "Error occurred while checking inventory";
        }

        boolean allProductsInStock = Arrays.stream(responses)
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
            return "Order Placed Successfully";
        } else {
            return "Product is not in stock";
        }
    }


    private OrderLineItem mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemsDto.getPrice());
        orderLineItem.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItem;
    }
}
