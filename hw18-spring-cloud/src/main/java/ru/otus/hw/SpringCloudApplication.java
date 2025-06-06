package ru.otus.hw;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@SpringBootApplication
public class SpringCloudApplication {

	public static void main(String[] args) {
        SpringApplication.run(SpringCloudApplication.class, args);
        System.out.println("Login http://localhost:8080/login");
        System.out.println("Authors http://localhost:8080/authors");
		System.out.println("Genres http://localhost:8080/genres");
		System.out.println("Books http://localhost:8080/books");
		System.out.println("Books http://localhost:8080/actuator");
		System.out.println("Books http://localhost:8080/explorer");
	}

}
