package dominio;

/**
 * Enum que define os 7 tipos de Tetrominós e suas rotações.
 */
public enum TipoTetromino {
    I(new boolean[][][]{
        {{true, true, true, true}},
        {{true}, {true}, {true}, {true}},
        {{true, true, true, true}},
        {{true}, {true}, {true}, {true}}
    }),
    O(new boolean[][][]{
        {{true, true}, {true, true}},
        {{true, true}, {true, true}},
        {{true, true}, {true, true}},
        {{true, true}, {true, true}}
    }),
    T(new boolean[][][]{
        {{false, true, false}, {true, true, true}},
        {{true, false}, {true, true}, {true, false}},
        {{true, true, true}, {false, true, false}},
        {{false, true}, {true, true}, {false, true}}
    }),
    S(new boolean[][][]{
        {{false, true, true}, {true, true, false}},
        {{true, false}, {true, true}, {false, true}},
        {{false, true, true}, {true, true, false}},
        {{true, false}, {true, true}, {false, true}}
    }),
    Z(new boolean[][][]{
        {{true, true, false}, {false, true, true}},
        {{false, true}, {true, true}, {true, false}},
        {{true, true, false}, {false, true, true}},
        {{false, true}, {true, true}, {true, false}}
    }),
    J(new boolean[][][]{
        {{true, false, false}, {true, true, true}},
        {{true, true}, {true, false}, {true, false}},
        {{true, true, true}, {false, false, true}},
        {{false, true}, {false, true}, {true, true}}
    }),
    L(new boolean[][][]{
        {{false, false, true}, {true, true, true}},
        {{true, false}, {true, false}, {true, true}},
        {{true, true, true}, {true, false, false}},
        {{true, true}, {false, true}, {false, true}}
    }),
    BIG(new boolean[][][]{
        {{true, true, true}, {true, true, true}, {true, true, true}},
        {{true, true, true}, {true, true, true}, {true, true, true}},
        {{true, true, true}, {true, true, true}, {true, true, true}},
        {{true, true, true}, {true, true, true}, {true, true, true}}
    });

    private final boolean[][][] formas;

    TipoTetromino(boolean[][][] formas) {
        this.formas = formas;
    }

    public boolean[][] getForma(int rotacao) {
        return formas[rotacao % formas.length];
    }

    public int getNumRotacoes() {
        return formas.length;
    }
}
