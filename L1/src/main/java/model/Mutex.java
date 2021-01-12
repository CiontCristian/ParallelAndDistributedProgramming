package model;

import lombok.Data;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Mutex {
    private Lock mutex;

    public Mutex(){
        mutex = new ReentrantLock();
    }

    public void lock(){
        mutex.lock();
    }

    public void unlock(){
        mutex.unlock();
    }
}
