package com.pavikumbhar.javaheart.springconfiguration;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import com.pavikumbhar.javaheart.messaging.MessageReceiver;

@Configuration
@EnableJms
public class MessagingConfiguration {

	private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";

	private static final String ORDER_QUEUE = "jms/destinationOne";

	@Bean
	public JndiTemplate jndiTemplate() {
		JndiTemplate jndiTemplate = new JndiTemplate();
		Properties environment = new Properties();
		environment.setProperty("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
		environment.setProperty("java.naming.provider.url", "t3://localhost:7001");
		// environment.setProperty("java.naming.security.principal",
		// "weblogic");
		// environment.setProperty("java.naming.security.credentials",
		// "weblogic");

		jndiTemplate.setEnvironment(environment);
		return jndiTemplate;
	}

	@Bean
	public JndiObjectFactoryBean jmsConnectionFactory() {
		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();

		jndiObjectFactoryBean.setJndiTemplate(jndiTemplate());
		jndiObjectFactoryBean.setJndiName("jms/ConnectionFactory");

		return jndiObjectFactoryBean;
	}


	@Bean
	public ConnectionFactory connectionFactory() {
		return ((ConnectionFactory) jmsConnectionFactory().getObject());
	}

	@Bean
	public JndiObjectFactoryBean destinationTwo() {
		JndiObjectFactoryBean destinationTwo = new JndiObjectFactoryBean();
		destinationTwo.setJndiTemplate(jndiTemplate());
		destinationTwo.setJndiName("jms/destinationTwo");
		return destinationTwo;
	}

	@Bean
	public JndiObjectFactoryBean destinationOne() {
		JndiObjectFactoryBean destinationOne = new JndiObjectFactoryBean();
		destinationOne.setJndiTemplate(jndiTemplate());
		destinationOne.setJndiName("jms/destinationOne");
		return destinationOne;
	}

	@Bean
	public JndiDestinationResolver jmsDestinationResolver() {
		JndiDestinationResolver jmsDestinationResolver = new JndiDestinationResolver();
		jmsDestinationResolver.setJndiTemplate(jndiTemplate());
		jmsDestinationResolver.setCache(true);
		return jmsDestinationResolver;
	}

	@Bean
	public JmsTemplate jmsTemplateForQueueTwo() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory());
		jmsTemplate.setDefaultDestination((Destination) destinationTwo().getObject());
		jmsTemplate.setDestinationResolver(jmsDestinationResolver());
		jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		jmsTemplate.setSessionTransacted(false); // for JTA
		return jmsTemplate;
	}
	
  
  @Bean
  public DefaultMessageListenerContainer queueMessageListener() {
	    DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
	    defaultMessageListenerContainer.setConnectionFactory((ConnectionFactory) jmsConnectionFactory().getObject());
	    defaultMessageListenerContainer.setDestination((Destination) destinationOne().getObject());
	    defaultMessageListenerContainer.setMessageListener(messageReceiver());
	    defaultMessageListenerContainer.setSessionTransacted(false);
	    defaultMessageListenerContainer.setConcurrentConsumers(1);
	    defaultMessageListenerContainer.setMaxConcurrentConsumers(40);
	    defaultMessageListenerContainer.afterPropertiesSet();
	    //defaultMessageListenerContainer.start();
	    return defaultMessageListenerContainer;
}

	@Bean
	public MessageReceiver messageReceiver(){
		return new MessageReceiver();
}
}
