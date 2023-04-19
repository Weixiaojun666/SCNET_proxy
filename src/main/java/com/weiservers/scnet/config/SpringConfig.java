package com.weiservers.scnet.config;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@Configuration
@ComponentScan("com.weiservers.scnet")
//@EnableAspectJAutoProxy//启动AOP的注解驱动
public class SpringConfig {
}
