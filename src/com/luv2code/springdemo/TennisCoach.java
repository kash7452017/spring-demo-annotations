package com.luv2code.springdemo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
//@Scope("prototype")
public class TennisCoach implements Coach  {

	@Autowired
	@Qualifier("randomFortuneService")
	private FortuneService fortuneService;
	
	// define a default constructor
	public TennisCoach()
	{
		System.out.println(">> TennisCoach: inside default constructor");
	}
	
	
	// define my init method
	@PostConstruct
	public void doMyStartupStuff()
	{
		System.out.println(">> TennisCoach: inside of doMyStartupStuff()");
	}
	
//	@Override
//	public void destroy() throws Exception {
//		System.out.println(">> TennisCoach: inside destroy()");
//		
//	}
	
	@PreDestroy
	// define my destroy method
	public void doMyCleanupStuff()
	{
		System.out.println(">> TennisCoach: inside of doMyCleanupStuff()");
	}
	
//	@Autowired
//	public void doSomeCrazyStuff(FortuneService theFortuneService) {
//		fortuneService = theFortuneService;
//		System.out.println(">> TennisCoach: inside doSomeCrazyStuff method");
//	}
	
//	@Autowired
//	public TennisCoach(FortuneService theFortuneService)
//	{
//		fortuneService = theFortuneService;
//	}
	
	@Override
	public String getDailyWorkout() {
		return "Practice your backhand volley";
	}

	@Override
	public String getDailyFortune() {
		// TODO Auto-generated method stub
		return fortuneService.getFortune();
	}

}
