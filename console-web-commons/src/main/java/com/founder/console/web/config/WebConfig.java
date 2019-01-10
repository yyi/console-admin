package com.founder.console.web.config;

import com.founder.console.web.config.annotation.WebController;
import com.founder.console.web.config.annotation.WebControllerAdvice;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;


//@Import(ShiroConfig.class)
@EnableWebMvc
@ComponentScan(
        basePackages = "com",
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter({WebController.class,WebControllerAdvice.class})
)
public class WebConfig extends AbstractWebConfiguration {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**","/spa/**").addResourceLocations("classpath:/static/static/","classpath:/static/spa/");
    }


   /* @PostConstruct
    public void init() {
        System.out.println("-----------------------WebConfig");
    }




    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan(new String[]{"com."});
        return marshaller;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CurrentUserMethodArgumentResolver());
        super.addArgumentResolvers(argumentResolvers);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/static/");
    }


    @Autowired
    Marshaller marshaller;
    @Autowired
    Unmarshaller unmarshaller;

//    @Autowired
//    SpringValidatorAdapter validator;

    @Override
    public void configureMessageConverters(
            List<HttpMessageConverter<?>> converters
    ) {
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new FormHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<>());

        MarshallingHttpMessageConverter xmlConverter =
                new MarshallingHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(Arrays.asList(
                new MediaType("application", "xml"),
                new MediaType("text", "xml")
        ));
        xmlConverter.setMarshaller(this.marshaller);
        xmlConverter.setUnmarshaller(this.unmarshaller);
        converters.add(xmlConverter);

        MappingJackson2HttpMessageConverter jsonConverter =
                new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Arrays.asList(
                new MediaType("application", "json"),
                new MediaType("text", "json")
        ));
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        jsonConverter.setObjectMapper(mapper);
        converters.add(jsonConverter);
    }

    @Override
    public void configureContentNegotiation(
            ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true).favorParameter(false)
                .parameterName("mediaType").ignoreAcceptHeader(false)
                .useJaf(false).defaultContentType(MediaType.APPLICATION_XML)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }

//    @Override
//    public Validator getValidator()
//    {
//        return this.validator;
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);

        registry.addInterceptor(new LocaleChangeInterceptor());
        registry.addInterceptor(new PermissionInterceptor());
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("ConsoleBackExecutor-");
        executor.initialize();
        return executor;
    }
*/


}
