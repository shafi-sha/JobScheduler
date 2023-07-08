package com.christ.job.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@ComponentScan({"com.christ.job.services", "com.christ.job.services.common", "com.christ.job.services.config"})
public class JobSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobSchedulerApplication.class, args);
	}

	@PostConstruct
	public void init(){
		// Setting timeZone globally
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
	}

}
