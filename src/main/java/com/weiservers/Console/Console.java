package com.weiservers.Console;

import com.weiservers.Core.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;


public class Console extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(Console.class);

    public void run() {
        while (!isInterrupted()) {
            Scanner scanner = new Scanner(System.in);
            ThreadPool.execute(new Command(scanner.nextLine()));
        }
    }
}
