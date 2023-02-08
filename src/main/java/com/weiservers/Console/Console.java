package com.weiservers.Console;

import com.weiservers.Core.ThreadPool;

import java.util.Scanner;


public class Console extends Thread {

    public void run() {
        while (!isInterrupted()) {
            Scanner scanner = new Scanner(System.in);
            ThreadPool.execute(new Command(scanner.nextLine()));
        }
    }
}
