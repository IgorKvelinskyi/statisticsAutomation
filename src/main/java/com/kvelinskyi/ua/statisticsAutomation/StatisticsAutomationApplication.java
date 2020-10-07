package com.kvelinskyi.ua.statisticsAutomation;

import com.kvelinskyi.ua.statisticsAutomation.helper.uploadingfiles.StorageProperties;
import com.kvelinskyi.ua.statisticsAutomation.helper.uploadingfiles.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class StatisticsAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatisticsAutomationApplication.class, args);
	}

	@Bean(name = "deleteTimeFiles")
	public CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

}
