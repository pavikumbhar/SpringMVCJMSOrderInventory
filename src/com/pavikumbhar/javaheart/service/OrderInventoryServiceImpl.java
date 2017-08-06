package com.pavikumbhar.javaheart.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pavikumbhar.javaheart.messaging.MessageSender;
import com.pavikumbhar.javaheart.model.InventoryResponse;
import com.pavikumbhar.javaheart.model.Order;



@Service("orderInventoryService")
public class OrderInventoryServiceImpl implements OrderInventoryService{
	static final Logger LOG = LoggerFactory.getLogger(OrderInventoryServiceImpl.class);
	
	@Autowired
	MessageSender messageSender;
	
	@Override
	public void processOrder(Order order) {
		
		//Perform any business logic.
		
		InventoryResponse response = prepareResponse(order);
		System.out.println("Inventory : sending order confirmation {}"+ response);
		messageSender.sendMessage(response);
	}
	
	private InventoryResponse prepareResponse(Order order){
		InventoryResponse response = new InventoryResponse();
		response.setOrderId(order.getOrderId());
		response.setReturnCode(200);
		response.setComment("Order Processed successfully");
		return response;
	}

	
	
}
