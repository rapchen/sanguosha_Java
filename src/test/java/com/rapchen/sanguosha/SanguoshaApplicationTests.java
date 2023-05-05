package com.rapchen.sanguosha;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
class SanguoshaApplicationTests {

    @Test
    void contextLoads() {
        Lock myLock = new ReentrantLock();
        myLock.lock(); // a ReentrantLock object
        try {
            // critical section
        } finally {
            myLock.unlock(); // make sure the lock is unlocked even if an exception is thrown
        }
    }

}
