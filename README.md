## Spring Configuration with Java Annotations
引用https://www.1ju.org/article/spring-component-annotation
>`@Component`是一個註釋，它允許Spring自動檢測我們的自定義bean。換句話說，無需編寫任何顯式代碼
>>* 掃描我們的應用程序以查找帶有@Component
>>* 實例化它們並將任何指定的依賴項注入到它們中
>>* 隨時隨地註入

>Spring提供了一些專門的@Controller，@Service 和@Repository。它們都提供與@Component相同的功能。它們都具有相同作用的原因是它們都是由@Component作為每個註釋的元註釋組成的註釋。它們就像@Component別名，具有特殊用途，並且在Spring自動檢測或依賴項注入之外具有含義。

**Spring中，默認Bean ID為類別名稱第一個字母小寫(EX：tennisCoach)，也可自訂Bean ID(EX：@Component(Bean Name))**

```
@Component
public class TennisCoach implements Coach  {

    @Override
	public String getDailyWorkout() {
		return "Practice your backhand volley";
	}
}
```
**測試範例以Spring本身默認Bean ID**
```
public class AnnotationDemoApp {

	public static void main(String[] args) {

		// read spring config file
		ClassPathXmlApplicationContext context =
				new ClassPathXmlApplicationContext("applicationContext.xml");
		
		// get the bean from spring container
		Coach theCoach = context.getBean("tennisCoach", Coach.class);
		
		// call a method on the bean
		System.out.println(theCoach.getDailyWorkout());
		
		// close the context
		context.close();
	}
}
```
**以下範例掩飾透過JAVA Annotation方式以及三種注入**
```
@Component
public class TennisCoach implements Coach  {
    
    // Field injection，Spring會自動找尋實現方法自動注入
    @Autowired
    private FortuneService fortuneService;
    
    // Constructor Injection
    @Autowired
	public TennisCoach(FortuneService theFortuneService)
	{
		fortuneService = theFortuneService;
	}
	
	// Setter Injection
	// 因搭配@Autowired，系統會自動找尋實現方法，因此，方法名稱不設限
	@Autowired
	public void doSomeCrazyStuff(FortuneService theFortuneService) {
		fortuneService = theFortuneService;
		System.out.println(">> TennisCoach: inside doSomeCrazyStuff method");
	}
    
    @Override
	public String getDailyWorkout() {
		return "Practice your backhand volley";
	}
}
```
**當有多個實現方法，可透過@Qualifier註解給定Bean ID，以下範例以默認Bean ID示範**
```
@Component
public class TennisCoach implements Coach  {

    @Autowired
    @Qualifier("randomFortuneService")
    private FortuneService fortuneService;
}
```
>一般來說，在使用Annotations時，對於默認的bean名稱，Spring使用如下規則。如果註釋的值不表示 bean 名稱，將根據類的短名稱（首字母小寫）構建適當的名稱。例如：HappyFortuneService --> happyFortuneService
>
>但是，對於類名的第一個和第二個字符都是大寫的特殊情況，則名稱不會被轉換。對於RESTFortuneService的情況RESTFortune Service --> RESTFortune Service沒有轉換，因為前兩個字符是大寫的。

**對於Bean Scope可透過`@Scope("prototype")`註解定義，以下為預設單例示範，並透過`@PostConstruct`、`@PreDestroy`自定義初始化與銷毀方法；`prototype`同樣要透過DisposableBean接口，請參考https://www.cnblogs.com/myseries/p/13453146.html**

```
@Component
public class TennisCoach implements Coach  {
    
    // define my init method
	@PostConstruct
	public void doMyStartupStuff()
	{
		System.out.println(">> TennisCoach: inside of doMyStartupStuff()");
	}
	
	@PreDestroy
	// define my destroy method
	public void doMyCleanupStuff()
	{
		System.out.println(">> TennisCoach: inside of doMyCleanupStuff()");
	}
}
```
## 純JAVA Code，沒有XML配置
>引用https://blog.csdn.net/u010013573/article/details/86650493
>
>使用`@Configuration`來註解類，在類裡面包含多個@Bean註解的方法。這些使用@Bean註解的方法，會被加載為`BeanFactory`裡面的`BeanDefinition`，其中beanName默認為方法名，並且默認會創建對應的bean對象實例，其中bean默認單例的。其實@Configuration註解的類，就相當於一個xml配置文。
>
>`@ComponentScan`：指定需要掃描的包。基於Spring的componentScan，因為@Configuration註解自身也是一個@Component。可以是使用xml或者使用@ComponentScan註解，@ComponentScan註解默認是掃描當前類所在的包及其子包
>
>@PropertySource：為Environment提供propertySource，即指定的屬性源的屬性鍵值也會加載到Environment中，其中Environment主要用來存放類路徑下的相關屬性文件，如properties文件的內容，是spring容器啟動時，最先加載的，即在加載bean之前加載，在創建beanDefinition或bean實例對象時就可以直接使用了
>
>
>* 配置類不能是final 類（沒法動態代理）
>* 配置類必須是非本地的（即不能將配置類定義在其他類的方法內部，不能是private）
>* 配置類必須有一個無參構造函數
```
@Configuration
//@ComponentScan("com.luv2code.springdemo")
@PropertySource("classpath:sport.properties")
public class SportConfig {

	// define bean for our sad fortune service
	@Bean
	public FortuneService sadFortuneService()
	{
		return new SadFortuneService();
	}
	
	// define bean for our swim coach AND inject dependency
	@Bean
	public Coach swimCoach()
	{
		return new SwimCoach(sadFortuneService());
	}
}
```
**sport.properties文件**
```
foo.email=myeasycoach@luv2code.com
foo.team=Awesome Java Coders
```
**新增`email`與`team`屬性，以及Getter方法並加入屬性之值的註釋，給出`${foo.email}`與`${foo.team}`屬性名稱**
```
@Component
public class SwimCoach implements Coach {
	
	private FortuneService fortuneService;
	
	@Value("${foo.email}")
	private String email;
	
	@Value("${foo.team}")
	private String team;
	
	public SwimCoach(FortuneService theFortuneService)
	{
		fortuneService = theFortuneService;
	}
	
	public String getEmail() {
		return email;
	}

	public String getTeam() {
		return team;
	}
}
```
