package com.example.vadim.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class Parser {

    private static String expr;
    private static int iter = 0;
    private static Lexem lexem;
    private static BigDecimal value;

    private static HashMap<Character, Lexem> map;

    static {
        map = new HashMap<>();
        map.put('+', Lexem.ADD);
        map.put('-', Lexem.SUB);
        map.put('*', Lexem.MUL);
        map.put('/', Lexem.DIV);
        map.put('(', Lexem.OPEN);
        map.put(')', Lexem.CLOSE);
    }

    private enum Lexem {
        ADD, SUB, MUL, DIV, OPEN, CLOSE, NUM, END
    }

    private static void takeNextLexem() throws ArithmeticException {
        if (iter == expr.length()) {
            lexem = Lexem.END;
            return;
        }
        if (map.containsKey(expr.charAt(iter))) {
            lexem = map.get(expr.charAt(iter));
            ++iter;
            return;
        }
        lexem = Lexem.NUM;
        value = BigDecimal.ZERO;
        if (expr.charAt(iter) == '.') {
            throw new ArithmeticException("'.' is not after a number");
        }
        boolean wasDot = false;
        BigDecimal after = BigDecimal.ONE;
        int offset = 0;
        while (iter < expr.length() && ((expr.charAt(iter) <= '9' && expr.charAt(iter) >= '0') ||
                expr.charAt(iter) == '.')) {
            if (expr.charAt(iter) == '.') {
                if (wasDot) {
                    throw new ArithmeticException("More than one '.' in a number");
                }
                wasDot = true;
            } else {
                if (wasDot) {
                    ++offset;
                }
                int digit = expr.charAt(iter) - '0';
                value = value.multiply(BigDecimal.TEN);
                value = value.add(BigDecimal.valueOf(digit));
            }
            ++iter;
        }
        value = value.movePointLeft(offset);
    }

    private static boolean good(Lexem lexem) {
        switch(lexem) {
            case ADD:
            case SUB:
            case NUM:
            case OPEN:
                return true;
            default:
                return false;
        }
    }

    private static BigDecimal sum() throws ArithmeticException {
        if (!good(lexem)) {
            throw new ArithmeticException("Invalid Expression");
        }
        BigDecimal ret = product();
        while (lexem == Lexem.ADD || lexem == Lexem.SUB) {
            Lexem temp = lexem;
            takeNextLexem();
            if (temp == Lexem.ADD) {
                ret = ret.add(product());
            } else {
                ret = ret.subtract(product());
            }
        }
        return ret;
    }

    private static BigDecimal product() throws ArithmeticException {
        if (!good(lexem)) {
            throw new ArithmeticException("InvalidExpression");
        }
        BigDecimal ret = item();
        while (lexem == Lexem.MUL || lexem == Lexem.DIV) {
            Lexem temp = lexem;
            takeNextLexem();
            if (temp == Lexem.MUL) {
                ret = ret.multiply(item());
            } else {
                ret = ret.divide(item(), 50, RoundingMode.HALF_EVEN);
            }
        }
        return ret;
    }

    private static BigDecimal item() throws ArithmeticException {
        BigDecimal temp;
        switch(lexem) {
            case SUB:
                takeNextLexem();
                return item().negate();
            case ADD:
                takeNextLexem();
                return item();
            case NUM:
                temp = value;
                takeNextLexem();
                return temp;
            case OPEN:
                takeNextLexem();
                temp = sum();
                if (lexem != Lexem.CLOSE) {
                    throw new ArithmeticException("InvalidExpression");
                }
                takeNextLexem();
                return temp;
            default:
                throw new ArithmeticException("InvalidExpression");
        }
    }

    public static BigDecimal getResult(String toParse) throws ArithmeticException {
        expr = toParse;
        iter = 0;
        takeNextLexem();
        BigDecimal answer = sum();
        if (lexem != Lexem.END) {
            throw new ArithmeticException("Invalid Expression");
        }
        return answer;
    }
}