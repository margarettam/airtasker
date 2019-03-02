package airtasker.technical.challenge.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import airtasker.technical.challenge.interceptor.RateLimitingInterceptor;

@Configuration
public class MainMvcConfig implements WebMvcConfigurer {
	
	@Autowired
	private RateLimitingInterceptor interceptor;
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}
