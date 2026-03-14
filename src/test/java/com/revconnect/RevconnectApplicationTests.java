package com.revconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RevconnectApplicationTests {

    @Test
    void applicationClassLoads() {
        assertNotNull(new RevconnectApplication());
    }
}