package crowdsourceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;

import crowdsourceservice.component.CrowdsourceService;
import crowdsourceservice.controller.CrowdsourceController;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackageClasses = {
		CrowdsourceController.class,
		CrowdsourceService.class
	})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public Docket geocodingApi() { 
        return new Docket(DocumentationType.SWAGGER_2)
          .groupName("excell-crowdsource-api")
          .select()
          	//.apis(RequestHandlerSelectors.any()) 
          	//.paths(PathSelectors.any())
          .build()
          .genericModelSubstitutes(ResponseEntity.class)
          //.protocols(Sets.newHashSet("https"))
//          .host("localhost:44445")
          .host("excell.vkm.tu-dresden.de:20080/excell-crowdsource-api")
          .apiInfo(apiInfo())
          ;
    }
    
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
          "ExCELL Crowdsource Event API",
          "This API provides a correctly mapped traffic event for given list of coordinates with timestamps.",
          "Version 1.0",
          "Use only for testing",
          "fkunde@beuth-hochschule",
          "Apache 2",
          "http://www.apache.org/licenses/LICENSE-2.0");
        return apiInfo;
    }
}