package se.kumliens.concept2runkeeper;

import com.vaadin.ui.UI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.vaadin.addon.oauthpopup.OAuthPopupUI;

@SpringBootApplication
public class Concept2runkeeperApplication {

	public static void main(String[] args) {
		SpringApplication.run(Concept2runkeeperApplication.class, args);
	}

}
