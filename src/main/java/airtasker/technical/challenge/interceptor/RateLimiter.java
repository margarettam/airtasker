package airtasker.technical.challenge.interceptor;

import org.springframework.beans.factory.annotation.Value;

public class RateLimiter {
	@Value("${rate.limit.capacity}")
    public String capacityTest;	
    @Value("${rate.limit.durationInMinutes}")
    
	public String durationInMinutes;
	private long tokens, capacity, durationInMillis, lastAccessTimestamp;	
	public RateLimiter(long capacity, long durationInMillis){
		this.capacity = capacity;
		this.tokens = capacity;
		this.durationInMillis = durationInMillis;
		this.lastAccessTimestamp = System.currentTimeMillis();		
	}
	
	public boolean allowAccess() {
		refillTokens();
		return (tokens-- > 0)?true:false; 
	}
	
	private void refillTokens() {
		long elapsedTime = System.currentTimeMillis()-lastAccessTimestamp;
		if(elapsedTime > durationInMillis) tokens = capacity;
		lastAccessTimestamp = System.currentTimeMillis();				
	}
	
	public long getNextRefillTime() {
		return lastAccessTimestamp + durationInMillis;
	}

}
