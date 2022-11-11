package ru.cynteka.task;

import info.debatty.java.stringsimilarity.JaroWinkler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        List<String> firstPathWords;
        List<String> secondPathWords;
        List<String> readFileList;
        List<String> similarWordsAnswer;
        try {
            readFileList = readFile("input.txt");
            int start = 1;
            int end = start + Integer.parseInt(readFileList.get(0));
            firstPathWords = readFileList.subList(start, end);
            secondPathWords = readFileList.subList(end+start, readFileList.size());
            similarWordsAnswer = returnListSimilarWords(firstPathWords, secondPathWords);
            Path out = Paths.get("output.txt");
            Files.write(out, similarWordsAnswer, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> returnListSimilarWords(List<String> firstList, List<String> secondList) {
        if (firstList.size() <= secondList.size()) {
            return findSimilarWords(firstList, secondList, true);
        }
        return findSimilarWords(firstList, secondList, false);
    }

    private static List<String> findSimilarWords(List<String> list1, List<String> list2, boolean equalsListSize) {
        List<String> similarWords = new ArrayList<>();
        Set<Integer> checkInclude = new HashSet<>();
        List<String> firstList = list1;
        List<String> secondList = list2;
        JaroWinkler jaroWinkler = new JaroWinkler();
        if (!equalsListSize) {
            firstList = list2;
            secondList = list1;
        }
        double min = 1;
        double minJaroWinkleDistance;
        int numberList = 0;
        for (int i = 0; i < firstList.size(); i++) {
            for (int j = 0; j < secondList.size(); j++) {
                minJaroWinkleDistance = jaroWinkler.distance(firstList.get(i), secondList.get(j));
                if (minJaroWinkleDistance < min) {
                    min = minJaroWinkleDistance;
                    numberList = j;
                }
            }
            min = 1;
            checkInclude.add(numberList);
            addSimilarWords(equalsListSize, similarWords, firstList, secondList, numberList, i);
        }
        checkWordsWhoNotSimilar(similarWords, checkInclude, secondList);
        return similarWords;
    }

    private static void addSimilarWords(boolean equalsListSize, List<String> similarWords, List<String> firstList, List<String> secondList, int numberList, int i) {
        if (!equalsListSize) {
            similarWords.add(secondList.get(numberList) + ":" + firstList.get(i));
            return;
        }
        similarWords.add(firstList.get(i) + ":" + secondList.get(numberList));
    }

    private static void checkWordsWhoNotSimilar(List<String> similarWords, Set<Integer> checkInclude, List<String> secondList) {
        for (int i = 0; i < secondList.size(); i++) {
            if (checkInclude.contains(i)) {
                continue;
            }
            similarWords.add(secondList.get(i) + ":?");
        }
    }

    private static List<String> readFile(String path) throws IOException {
        String readFile;
        List<String> list = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(path));
        while ((readFile = in.readLine()) != null) {
            if (readFile.isEmpty()) {
                continue;
            }
            list.add(readFile);
        }
        return list;
    }

}
