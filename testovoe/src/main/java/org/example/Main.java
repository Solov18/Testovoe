package org.example;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) {
        // Вес моркови на каждой полянке
        List<Integer> carrotWeights = Arrays.asList(1, 2, 3, 4, 5);
        // Максимальный вес, который можно унести за одну ходку
        int maxWeightPerTrip = 5;
        // Максимальное количество ходок
        int maxTrips = 10;

        // Подсчет и анализ эффективности методов
        analyzeEfficiency(carrotWeights, maxWeightPerTrip, maxTrips);
    }

    private static void analyzeEfficiency(List<Integer> weights, int maxWeight, int trips) {
        // Анализ метода с использованием Stream API
        long startTime1 = System.nanoTime();
        int[] iterations1 = new int[1];
        int totalCarrots1 = calculateMaxCarriedCarrotsStream(weights, maxWeight, trips, iterations1);
        long endTime1 = System.nanoTime();
        long time1 = endTime1 - startTime1;

        // Анализ метода с использованием циклов
        long startTime2 = System.nanoTime();
        int[] iterations2 = new int[1];
        int totalCarrots2 = calculateMaxCarriedCarrotsLoop(weights, maxWeight, trips, iterations2);
        long endTime2 = System.nanoTime();
        long time2 = endTime2 - startTime2;

        // Анализ метода с использованием рекурсивного подхода
        long startTime3 = System.nanoTime();
        int[] iterations3 = new int[1];
        int totalCarrots3 = calculateMaxCarriedCarrotsRecursive(weights, maxWeight, trips, iterations3);
        long endTime3 = System.nanoTime();
        long time3 = endTime3 - startTime3;

        // Анализ жадного метода
        long startTime4 = System.nanoTime();
        int[] iterations4 = new int[1];
        int totalCarrots4 = calculateMaxCarriedCarrotsGreedy(weights, maxWeight, trips, iterations4);
        long endTime4 = System.nanoTime();
        long time4 = endTime4 - startTime4;

        // Вывод результатов анализа
        System.out.printf("Stream API            - Всего морковок: %-2d, Время: %-10d ns, Итерации: %-2d%n", totalCarrots1, time1, iterations1[0]);
        System.out.printf("Циклы                 - Всего морковок: %-2d, Время: %-10d ns, Итерации: %-2d%n", totalCarrots2, time2, iterations2[0]);
        System.out.printf("Рекурсивный подход    - Всего морковок: %-2d, Время: %-10d ns, Итерации: %-2d%n", totalCarrots3, time3, iterations3[0]);
        System.out.printf("Жадный алгоритм       - Всего морковок: %-2d, Время: %-10d ns, Итерации: %-2d%n", totalCarrots4, time4, iterations4[0]);

        // Определение самого эффективного метода
        long minTime = Math.min(Math.min(time1, time2), Math.min(time3, time4));
        String bestMethod = "";
        if (minTime == time1) {
            bestMethod = "Stream API";
        } else if (minTime == time2) {
            bestMethod = "Циклы";
        } else if (minTime == time3) {
            bestMethod = "Рекурсивный подход";
        } else if (minTime == time4) {
            bestMethod = "Жадный алгоритм";
        }

        System.out.println("Самый эффективный метод: " + bestMethod);
    }

    // Метод 1: Использование Stream API для подсчета максимального количества моркови
    private static int calculateMaxCarriedCarrotsStream(List<Integer> weights, int maxWeight, int trips, int[] iterations) {
        List<Integer> sortedWeights = weights.stream().sorted().collect(Collectors.toList());

        return Stream.generate(() -> 0)
                .limit(trips)
                .mapToInt(i -> {
                    int[] tripData = sortedWeights.stream()
                            .flatMap(weight -> Stream.iterate(weight, w -> w).limit(maxWeight / weight))
                            .filter(weight -> weight <= maxWeight)
                            .reduce(new int[]{0, 0}, (acc, weight) -> {
                                iterations[0]++;
                                if (acc[0] + weight <= maxWeight) {
                                    acc[0] += weight;
                                    acc[1]++;
                                }
                                return acc;
                            }, (acc1, acc2) -> acc1);

                    return tripData[1];
                })
                .sum();
    }

    // Метод 2: Использование циклов для подсчета максимального количества моркови
    private static int calculateMaxCarriedCarrotsLoop(List<Integer> weights, int maxWeight, int trips, int[] iterations) {
        weights = weights.stream().sorted().collect(Collectors.toList());

        int totalCarrots = 0;
        int remainingTrips = trips;

        while (remainingTrips > 0) {
            int currentTripWeight = 0;
            int currentTripCarrots = 0;

            for (int weight : weights) {
                while (currentTripWeight + weight <= maxWeight) {
                    iterations[0]++;
                    currentTripWeight += weight;
                    currentTripCarrots++;
                }
            }

            totalCarrots += currentTripCarrots;
            remainingTrips--;
        }

        return totalCarrots;
    }

    // Метод 3: Использование рекурсивного подхода для подсчета максимального количества моркови
    private static int calculateMaxCarriedCarrotsRecursive(List<Integer> weights, int maxWeight, int trips, int[] iterations) {
        List<Integer> sortedWeights = weights.stream().sorted().collect(Collectors.toList());

        return IntStream.range(0, trips)
                .map(i -> calculateTripCarrots(sortedWeights, maxWeight, iterations))
                .sum();
    }

    private static int calculateTripCarrots(List<Integer> weights, int maxWeight, int[] iterations) {
        int currentTripWeight = 0;
        int currentTripCarrots = 0;

        for (int weight : weights) {
            while (currentTripWeight + weight <= maxWeight) {
                iterations[0]++;
                currentTripWeight += weight;
                currentTripCarrots++;
            }
        }

        return currentTripCarrots;
    }

    // Метод 4: Жадный алгоритм для подсчета максимального количества моркови
    private static int calculateMaxCarriedCarrotsGreedy(List<Integer> weights, int maxWeight, int trips, int[] iterations) {
        // Сортируем морковь по весу, чтобы начать с самой легкой
        List<Integer> sortedWeights = weights.stream().sorted().collect(Collectors.toList());

        int totalCarrots = 0;

        for (int i = 0; i < trips; i++) {
            int currentTripWeight = 0;
            for (int weight : sortedWeights) {
                while (currentTripWeight + weight <= maxWeight) {
                    iterations[0]++;
                    currentTripWeight += weight;
                    totalCarrots++;
                }
            }
        }

        return totalCarrots;
    }
}