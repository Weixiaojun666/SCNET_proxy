package com.weiservers.scnet.thread;

import com.weiservers.scnet.thread.child.Command;
import com.weiservers.scnet.utils.ThreadPool;

import java.util.Scanner;


public class Console extends Thread {
    @Override
    public void run() {
        while (!isInterrupted()) {
            Scanner scanner = new Scanner(System.in);
            ThreadPool.execute(new Command(scanner.nextLine()));
        }
    }
}
