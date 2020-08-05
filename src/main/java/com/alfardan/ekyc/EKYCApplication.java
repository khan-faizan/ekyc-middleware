package com.alfardan.ekyc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages= {"com.alfardan.ekyc.*"})
@ComponentScan(basePackages = "com.alfardan.ekyc.*")
@EnableAutoConfiguration

public class EKYCApplication {
	
	private static final Logger log = LoggerFactory.getLogger(EKYCApplication.class);
	
	
	private final Environment env;

    public EKYCApplication(Environment env) {
        this.env = env;
    }
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(EKYCApplication.class);
		//SpringApplication.run(EKYCApplication.class, args);
		
		Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
	}
	
	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
	  TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
	      @Override
	      protected void postProcessContext(Context context) {
	        SecurityConstraint securityConstraint = new SecurityConstraint();
	        securityConstraint.setUserConstraint("CONFIDENTIAL");
	        SecurityCollection collection = new SecurityCollection();
	        collection.addPattern("/*");
	        securityConstraint.addCollection(collection);
	        context.addConstraint(securityConstraint);
	      }
	    };
	  
	  tomcat.addAdditionalTomcatConnectors(redirectConnector());
	  return tomcat;
	}

	private Connector redirectConnector() {
	  Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
	  connector.setScheme("http");
	  connector.setPort(8080);
	  connector.setSecure(true);
	  
	  String serverPort = env.getProperty("server.port");
	  connector.setRedirectPort(Integer.parseInt(serverPort));
	  
	  return connector;
	}
	
	
	
	private static void logApplicationStartup(Environment env) {
    	System.out.println("ENV::"+env);
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = "/";
//        String contextPath = env.getProperty("server.servlet.context-path");
//        if (contextPath.isEmpty()) {
//            contextPath = "/";
//        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }
}
//@RestController
//class SecuredServerController{
//	
//	@RequestMapping("/secured")
//	public String secured(){
//		System.out.println("Inside secured()");
//		return "Hello user !!! : " + new Date();
//	}
//}

