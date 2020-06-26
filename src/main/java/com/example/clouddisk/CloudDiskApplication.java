package com.example.clouddisk;

import com.example.clouddisk.models.CloudFile;
import com.example.clouddisk.models.Disk;
import com.example.clouddisk.models.User;
import com.example.clouddisk.repos.DiskRepo;
import com.example.clouddisk.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class CloudDiskApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudDiskApplication.class, args);
	}



	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).
				select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
}

