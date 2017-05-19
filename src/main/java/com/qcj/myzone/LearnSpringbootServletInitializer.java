package com.qcj.myzone;

import org.springframework.boot.builder.SpringApplicationBuilder;  
import org.springframework.boot.web.support.SpringBootServletInitializer;  //   
  
public class LearnSpringbootServletInitializer extends SpringBootServletInitializer {  
    @Override  
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {  
        return builder.sources(App.class);  
    }  
  
}