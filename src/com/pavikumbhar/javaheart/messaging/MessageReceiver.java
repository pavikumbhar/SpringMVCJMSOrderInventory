package com.pavikumbhar.javaheart.messaging;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import com.pavikumbhar.javaheart.model.Order;
import com.pavikumbhar.javaheart.service.OrderInventoryService;

@Component
public class MessageReceiver implements MessageListener{
	static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

	private static final String ORDER_RESPONSE_QUEUE = "jms/destinationTwo";
	
	@Autowired
	OrderInventoryService orderInventoryService;
	
	 ObjectMessage msgObj = null;
	
	
	public void receiveMessage(final Message<Order> message) throws JMSException {
		LOG.info("----------------------------------------------------");
		MessageHeaders headers =  message.getHeaders();
		LOG.info("Application : headers received : {}", headers);
		
		Order order = message.getPayload();
		LOG.info("Application : product : {}",order);	

		orderInventoryService.processOrder(order);
		LOG.info("----------------------------------------------------");

	}




	@Override
	public void onMessage(javax.jms.Message message) {
		System.out.println("+++++++++++Order Inventory++++++++++++++++++++++++");
		try {
			if (message instanceof ObjectMessage) {
				msgObj = (ObjectMessage) message;
			
			if(msgObj.getObject() instanceof Order){
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
			//MessageHeaders headers =  message.getHeaders();
			//LOG.info("Application : headers received : {}", headers);
			
			Order order = (Order)msgObj.getObject();
			System.out.println("Application : product : {}"+order);	

			orderInventoryService.processOrder(order);
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
			}else{
				System.out.println("+++++++++++++++++else>>>+++++++++++++++++++++++++++++++++++");
							
			}
		}
		
			 } catch (Exception ex) {
				 LOG.debug( ex.getMessage());
		}
		
		
	}
}
