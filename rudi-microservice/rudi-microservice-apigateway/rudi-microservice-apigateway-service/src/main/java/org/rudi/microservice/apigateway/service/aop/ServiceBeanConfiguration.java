package org.rudi.microservice.apigateway.service.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy()
@ComponentScan(basePackages = {"org.rudi.microservice.apigateway.service.aop"})
public class ServiceBeanConfiguration {


}
