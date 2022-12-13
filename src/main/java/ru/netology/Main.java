package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static int queueSize = 100;
    static BlockingQueue<String> firstThreadQueque = new ArrayBlockingQueue(queueSize);
    static BlockingQueue<String> secondThreadQueque = new ArrayBlockingQueue(queueSize);
    static BlockingQueue<String> thirdThreadQueque = new ArrayBlockingQueue(queueSize);
    static int wordSize = 100_000;
    static int numberOfWords = 10000;

    public static void main(String[] args) {
        String letters = "abc";

        Thread generationTexts = new Thread(() -> {
            for (int i = 0; i < numberOfWords; i++) {
                String word = generateText(letters, wordSize);
                try {
                    firstThreadQueque.put(word);
                    secondThreadQueque.put(word);
                    thirdThreadQueque.put(word);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        generationTexts.start();

        Thread countingA = new Thread(() -> {
            System.out.println("Max count A = " + oneLetterCount(firstThreadQueque, letters.charAt(0)));
        });
        countingA.start();

        Thread countingB = new Thread(() -> {
            System.out.println("Max count B = " + oneLetterCount(secondThreadQueque, letters.charAt(1)));
        });
        countingB.start();

        Thread countingC = new Thread(() -> {
            System.out.println("Max count C = " + oneLetterCount(thirdThreadQueque, letters.charAt(2)));
        });
        countingC.start();

        try {
            countingA.join();
            countingB.join();
            countingC.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }


    public static int oneLetterCount(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int maxCount = 0;
        for (int i = 0; i < numberOfWords; i++) {
            try {
                String word = queue.take();
                count = 0;
                for (char character : word.toCharArray()) {
                    if (character == letter) {
                        count++;
                    }
                }
                if (count > maxCount) maxCount = count;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        return count;
    }
}