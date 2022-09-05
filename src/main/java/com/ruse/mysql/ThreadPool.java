package com.ruse.mysql;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPool {

    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);

}
