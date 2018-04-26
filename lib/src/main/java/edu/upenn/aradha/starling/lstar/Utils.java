package edu.upenn.aradha.starling.lstar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Utils {

    public static List<List<Input>> wordsOfLength(List<Input> symbols, int length) {
        if (length == 0) {
            List<Input> epsilon = new ArrayList<>();
            List<List<Input>> epsilonLanguage = new ArrayList<>();
            epsilonLanguage.add(epsilon);
            return epsilonLanguage;
        } else {
            List<List<Input>> language = new ArrayList<>();
            List<List<Input>> subLanguage = wordsOfLength(symbols, length - 1);
            for (List<Input> subWord : subLanguage) {
                for (Input symbol : symbols) {
                    List<Input> word = new ArrayList<>();
                    word.add(symbol);
                    word.addAll(subWord);
                    language.add(word);
                }
            }
            return language;
        }
    }

    public static <E> String stringJoin(String separator, Collection<E> collection) {
        if (collection.isEmpty()) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (E e : collection) {
                if (first) {
                    first = false;
                } else {
                    result.append(separator);
                }
                result.append(e.toString());
            }
            return result.toString();
        }
    }
}
