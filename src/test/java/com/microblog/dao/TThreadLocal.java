package com.microblog.dao;

import lombok.SneakyThrows;

import java.time.LocalDateTime;

/**
 * @author 贺畅
 * @date 2023/3/28
 */
public class TThreadLocal {
	public static void main(String[] args) {
		Object lock = new Object();

		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				synchronized (lock) {
					System.out.println(LocalDateTime.now() + "aa");
					Thread.sleep(5000);
					lock.notify();
				}
			}
		}).start();
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				synchronized (lock) {
					lock.wait(20000);
					System.out.println(LocalDateTime.now() + "bb");
				}
			}
		}).start();
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				synchronized (lock) {
					lock.wait(15000);
					System.out.println(LocalDateTime.now() + "cc");
				}
			}
		}).start();
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				synchronized (lock) {
					lock.wait(10000);
					System.out.println(LocalDateTime.now() + "dd");
				}
			}
		}).start();
		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run() {
				synchronized (lock) {
					lock.wait(10000);
					System.out.println(LocalDateTime.now() + "ee");
				}
			}
		}).start();

	}
}
