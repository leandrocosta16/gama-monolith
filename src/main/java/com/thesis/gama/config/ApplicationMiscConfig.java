package com.thesis.gama.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationMiscConfig {
    //nao é o melhor para performance mas os bons tenho de criar uma interface para cada tipo lol
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
