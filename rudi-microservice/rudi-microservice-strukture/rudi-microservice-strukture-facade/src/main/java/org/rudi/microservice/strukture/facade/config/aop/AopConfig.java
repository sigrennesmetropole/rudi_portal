package org.rudi.microservice.strukture.facade.config.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = { "org.rudi.common.service.aop" })
public class AopConfig {

}
