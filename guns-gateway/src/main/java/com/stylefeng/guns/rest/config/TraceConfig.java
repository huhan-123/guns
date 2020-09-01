package com.stylefeng.guns.rest.config;

import brave.sampler.Sampler;
import brave.spring.beans.TracingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
public class TraceConfig {

	@Bean(name = "tracing")
	public TracingFactoryBean getTracingBean(){
		Sender sender = OkHttpSender.create("http://localhost:9411/api/v2/spans");
		AsyncReporter<Span> reporter = AsyncReporter.create(sender);
		Sampler sampler = Sampler.create(1); // 取样率


		TracingFactoryBean tracingFactoryBean = new TracingFactoryBean();
		tracingFactoryBean.setLocalServiceName("gateway");
		tracingFactoryBean.setSpanReporter(reporter);
		tracingFactoryBean.setSampler(sampler);
		return tracingFactoryBean;
	}

    /*@Bean
    public Tracing tracing(){
        Sender sender = OkHttpSender.create("http://localhost:9411/api/v2/spans");
        AsyncReporter<Span> reporter = AsyncReporter.builder(sender)
                .closeTimeout(5000, TimeUnit.MILLISECONDS)
                .messageTimeout(10000, TimeUnit.MILLISECONDS)
                .build();

        Tracing.newBuilder()
                .localServiceName("consumer1")
                .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY,"shiliew"))
                .sampler(Sampler.ALWAYS_SAMPLE)
                .spanReporter(reporter)
                .build();

        return tracing();
    }*/

}
