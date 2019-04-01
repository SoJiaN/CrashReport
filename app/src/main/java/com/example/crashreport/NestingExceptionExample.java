package com.example.crashreport;

/**
 * Copyright © 2013-2018 Worktile. All Rights Reserved.
 * Author: SongJian
 * Email: songjian@worktile.com
 * Date: 2019/3/29
 * Time: 18:27
 * Desc:
 */
public class NestingExceptionExample {

    public static void main(String[] args) throws Exception {
        Object[] localArgs = args;
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(e.getMessage());
                for (int i = 0; i < e.getStackTrace().length; i++) {
                    System.out.println(e.getStackTrace()[i]);
                }
            }
        });

        try {
            Integer[] numbers = (Integer[]) localArgs;
        } catch (ClassCastException originalException) {
            Exception generalException = new Exception("Horrible exception!", originalException);
            throw generalException;
        }
    }
}