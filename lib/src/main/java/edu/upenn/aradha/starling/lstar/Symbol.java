package edu.upenn.aradha.starling.lstar;

public abstract class Symbol {
    private final String symbol;
    private final String canonSymbol;

    public Symbol(String symbol, boolean isInput) {
        this.symbol = symbol;
        this.isInput = isInput;
        this.isOutput = !this.isInput;
        if (isInput)
            this.canonSymbol = "I " + symbol;
        else
            this.canonSymbol = "O " + symbol;
    }

    @Override
    public int hashCode() {
        return canonSymbol.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;
        else if (that instanceof Symbol)
            return this.canonSymbol.equals(((Symbol) that).canonSymbol);
        else
            return false;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public boolean isInput;
    public boolean isOutput;
}
