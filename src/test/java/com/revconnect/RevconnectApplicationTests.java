package com.revconnect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.*;

class RevconnectApplicationTest {

    @Test
    void testMainMethod() {

        SpringApplication app = mock(SpringApplication.class);

        RevconnectApplication.main(new String[]{});

    }
}