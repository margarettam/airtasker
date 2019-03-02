package airtasker.technical.challenge.interceptor;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.stereotype.Component;

@Component
public class RateLimitingInterceptor extends HandlerInterceptorAdapter {
	
	
	@Value("${rate.limit.capacity}")
	private int capacity;
	@Value("#{${rate.limit.durationInMinutes}*60*1000}")
	public int durationInMillis;
	private Map<String, RateLimiter> attenList = new ConcurrentHashMap<>();
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
		String clientIp = getClientIpAddress(request);
		if(clientIp != null) {
			RateLimiter limiter = getRateLimiter(clientIp);
			boolean allowAccess = limiter.allowAccess();
			
    		if(!allowAccess) {
    			response.sendError(
    					HttpStatus.TOO_MANY_REQUESTS.value(),
    					String.format("Rate limit exceeded. Try again in %d seconds", limiter.getNextRefillTime()/1000));    			    			
    		}
    		return allowAccess;
    		
    	}else {
    		return false;
    	}    	
    }
		
    private synchronized RateLimiter getRateLimiter(String clientIp) {    	
    		if(!attenList.containsKey(clientIp)) {
    			attenList.put(clientIp, new RateLimiter(capacity, durationInMillis));
    		}    	
        	return attenList.get(clientIp);	    	    			
    }
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null || "".equals(xForwardedForHeader)) {
            return request.getRemoteAddr();
        } else {
        	return xForwardedForHeader.contains(",")?xForwardedForHeader.split(",")[0]:xForwardedForHeader;
        }
    }
}
