package com.founder.console.web.config;

import com.founder.contract.sysadmin.DictionaryService;
import com.founder.utils.ConsoleUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.util.Map;
import java.util.concurrent.Executor;

public abstract class AbstractRootConfig extends SpringBootServletInitializer {

    @Value("${console.async.task.corePollSize:2}")
    protected int corePollSize;

    @Value("${console.async.task.maxPollSize:20}")
    protected int maxPollSize;

    @Value("${console.async.task.queueCapacity:500}")
    protected int queueCapacity;

    @Value("${console.async.task.threadNamePrefix:ConsoleBackExecutor-}")
    protected String threadNamePrefix;

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePollSize);
        executor.setMaxPoolSize(maxPollSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

    @Value("${console.pool.textRenderer.total.max:10}")
    private int textRendererTotalMax;

    @Value("${console.pool.textRenderer.idle.max:10}")
    private int textRendererIdleMax;

    @Value("${console.pool.textRenderer.idle.min:1}")
    private int textRendererIdleMin;

    @Autowired
    private DictionaryService dictionaryService;

    @Bean("PdfTextRenderer")
    public GenericObjectPool<ITextRenderer> textRendererPool() {
        GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
        conf.setMaxTotal(textRendererTotalMax);
        conf.setMaxIdle(textRendererIdleMax);
        conf.setMinIdle(textRendererIdleMin);
        conf.setJmxEnabled(false);
        Map<String, String> map = dictionaryService.getDictionaryMapByType("FONT_PATH");
        String sunFontPath = ConsoleUtils.isWindows() ?
                map.get("sun_font_path_windows")
                :
                map.get("sun_font_path_others");

        return new GenericObjectPool<>(new TextRendererPool(sunFontPath), conf);

    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Value("${console.web.rest.readTimeout:30000}")
    protected int restTimeout;

    @Value("${console.web.rest.connectTimeout:10000}")
    protected int connectTimeout;

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(restTimeout);
        factory.setConnectTimeout(connectTimeout);
        return factory;
    }

    @Autowired
    private ApplicationContext context;


    @Bean
    public DispatcherServletRegistrationBean webDispatcher() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(WebConfig.class);
        applicationContext.setParent(context);
        dispatcherServlet.setApplicationContext(applicationContext);
        DispatcherServletRegistrationBean servletRegistrationBean = new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        servletRegistrationBean.setLoadOnStartup(1);
        servletRegistrationBean.setName("webservlet");
        return servletRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean ajaxDispatcher() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(AjaxConfig.class);
        applicationContext.setParent(context);
        dispatcherServlet.setApplicationContext(applicationContext);
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/rest/*");
        servletRegistrationBean.setName("restfulservlet");
        servletRegistrationBean.setLoadOnStartup(2);
        return servletRegistrationBean;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(getBootClass());
    }

    abstract protected Class getBootClass();
}
