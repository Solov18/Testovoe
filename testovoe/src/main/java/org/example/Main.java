package org.example;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class Main {

    public static void main(String[] args) {
        // Определяем веса моркови на каждой полянке
        List<Integer> carrotWeights = Arrays.asList(1, 2, 3, 4, 5);
        Random random = new Random();

        // Генерируем случайное количество моркови на каждой полянке (от 1 до 10 морковок)
        List<Integer> carrotCounts = carrotWeights.stream()
                .map(weight -> random.nextInt(10) + 1) // Случайное количество от 1 до 10
                .collect(Collectors.toList());

        // Максимальный вес, который можно унести за одну ходку
        int maxWeightPerTrip = 5;
        // Максимальное количество ходок
        int maxTrips = 10;

        System.out.println("Изначальное количество моркови на каждой полянке: " + carrotCounts);

        // Подсчет и анализ эффективности методов
        analyzeEfficiency(carrotWeights, carrotCounts, maxWeightPerTrip, maxTrips);
    }

    private static void analyzeEfficiency(List<Integer> weights, List<Integer> counts, int maxWeight, int trips) {
        // Сохраняем оригинальные данные для каждого метода
        List<Integer> originalCounts = new ArrayList<>(counts);

        // Анализ метода с использованием Stream API
        long startTime1 = System.nanoTime();
        int[] iterations1 = new int[1];
        int totalCarrots1 = calculateMaxCarriedCarrotsStream(weights, originalCounts, maxWeight, trips, iterations1);
        long endTime1 = System.nanoTime();
        long time1 = endTime1 - startTime1;

        // Анализ метода с использованием циклов
        originalCounts.clear();
        originalCounts.addAll(counts);
        long startTime2 = System.nanoTime();
        int[] iterations2 = new int[1];
        int totalCarrots2 = calculateMaxCarriedCarrotsLoop(weights, originalCounts, maxWeight, trips, iterations2);
        long endTime2 = System.nanoTime();
        long time2 = endTime2 - startTime2;

        // Анализ метода с использованием рекурсивного подхода
        originalCounts.clear();
        originalCounts.addAll(counts);
        long startTime3 = System.nanoTime();
        int[] iterations3 = new int[1];
        int totalCarrots3 = calculateMaxCarriedCarrotsRecursive(weights, originalCounts, maxWeight, trips, iterations3);
        long endTime3 = System.nanoTime();
        long time3 = endTime3 - startTime3;

        // Анализ жадного метода
        originalCounts.clear();
        originalCounts.addAll(counts);
        long startTime4 = System.nanoTime();
        int[] iterations4 = new int[1];
        int totalCarrots4 = calculateMaxCarriedCarrotsGreedy(weights, originalCounts, maxWeight, trips, iterations4);
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
    private static int calculateMaxCarriedCarrotsStream(List<Integer> weights, List<Integer> counts, int maxWeight, int trips, int[] iterations) {
        List<Integer> sortedWeights = weights.stream().sorted().collect(Collectors.toList());

        return IntStream.range(0, trips)
                .map(i -> {
                    int tripWeight = 0;
                    int tripCarrots = 0;

                    for (int j = 0; j < sortedWeights.size(); j++) {
                        int weight = sortedWeights.get(j);
                        int count = counts.get(j);

                        while (tripWeight + weight <= maxWeight && count > 0) {
                            tripWeight += weight;
                            tripCarrots++;
                            count--;
                            iterations[0]++;
                        }

                        // Обновляем количество моркови на текущей полянке
                        counts.set(j, count);
                    }

                    return tripCarrots;
                })
                .sum();
    }

    // Метод 2: Использование циклов для подсчета максимального количества моркови
    private static int calculateMaxCarriedCarrotsLoop(List<Integer> weights, List<Integer> counts, int maxWeight, int trips, int[] iterations) {
        List<Integer> sortedWeights = weights.stream().sorted().collect(Collectors.toList());

        int totalCarrots = 0;

        for (int i = 0; i < trips; i++) {
            int tripWeight = 0;
            int tripCarrots = 0;

            for (int j = 0; j < sortedWeights.size(); j++) {
                int weight = sortedWeights.get(j);
                int count = counts.get(j);

                while (tripWeight + weight <= maxWeight && count > 0) {
                    tripWeight += weight;
                    tripCarrots++;
                    count--;
                    iterations[0]++;
                }

                // Обновляем количество моркови на текущей полянке
                counts.set(j, count);
            }

            totalCarrots += tripCarrots;
        }

        return totalCarrots;
    }

    // Метод 3: Использование рекурсивного подхода для подсчета максимального количества моркови
    private static int calculateMaxCarriedCarrotsRecursive(List<Integer> weights, List<Integer> counts, int maxWeight, int trips, int[] iterations) {
        List<Integer> sortedWeights = weights.stream().sorted().collect(Collectors.toList());

        return IntStream.range(0, trips)
                .map(i -> calculateTripCarrots(sortedWeights, counts, maxWeight, iterations))
                .sum();
    }

    private static int calculateTripCarrots(List<Integer> weights, List<Integer> counts, int maxWeight, int[] iterations) {
        int tripWeight = 0;
        int tripCarrots = 0;

        for (int i = 0; i < weights.size(); i++) {
            int weight = weights.get(i);
            int count = counts.get(i);

            while (tripWeight + weight <= maxWeight && count > 0) {
                tripWeight += weight;
                tripCarrots++;
                count--;
                iterations[0]++;
            }

            // Обновляем количество моркови на текущей полянке
            counts.set(i, count);
        }

        return tripCarrots;
    }

    // Метод 4: Жадный алгоритм для подсчета максимального количества моркови
    private static int calculateMaxCarriedCarrotsGreedy(List<Integer> weights, List<Integer> counts, int maxWeight, int trips, int[] iterations) {
        List<Integer> sortedWeights = weights.stream().sorted().collect(Collectors.toList());

        int totalCarrots = 0;

        for (int i = 0; i < trips; i++) {
            int tripWeight = 0;

            for (int j = 0; j < sortedWeights.size(); j++) {
                int weight = sortedWeights.get(j);
                int count = counts.get(j);

                while (tripWeight + weight <= maxWeight && count > 0) {
                    tripWeight += weight;
                    totalCarrots++;
                    count--;
                    iterations[0]++;
                }

                // Обновляем количество моркови на текущей полянке
                counts.set(j, count);
            }
        }

        return totalCarrots;
    }
}