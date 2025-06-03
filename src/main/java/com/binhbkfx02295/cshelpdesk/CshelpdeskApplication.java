package com.binhbkfx02295.cshelpdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Locale;

@SpringBootApplication
@EnableScheduling
public class CshelpdeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(CshelpdeskApplication.class, args);
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("messages");
		source.setDefaultEncoding("UTF-8");
		source.setDefaultLocale(Locale.forLanguageTag("vi"));
		return source;
	}

}
