package com.revconnect.security.jwt;

import org.springframework.stereotype.Service;

// LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class JwtService {

    private static final Logger logger =
            LogManager.getLogger(JwtService.class);

    public JwtService() {
        logger.info("JwtService initialized");
    }

}