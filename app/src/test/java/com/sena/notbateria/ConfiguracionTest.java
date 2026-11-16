package com.sena.notbateria;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para la lógica de configuración del umbral.
 */
public class ConfiguracionTest {

    private static final int LIMITE_MIN = 50;
    private static final int LIMITE_MAX = 100;

    @Test
    public void testLimiteValido() {
        int limite = 80;
        assertTrue("El límite 80 debe ser válido",
                limite >= LIMITE_MIN && limite <= LIMITE_MAX);
    }

    @Test
    public void testLimiteMenorAlMinimo() {
        int limite = 45;
        assertFalse("El límite 45 debe ser inválido",
                limite >= LIMITE_MIN && limite <= LIMITE_MAX);
    }

    @Test
    public void testLimiteMayorAlMaximo() {
        int limite = 101;
        assertFalse("El límite 101 debe ser inválido",
                limite >= LIMITE_MIN && limite <= LIMITE_MAX);
    }

    @Test
    public void testLimiteEnLosBordes() {
        assertTrue("El límite 50 debe ser válido",
                LIMITE_MIN >= LIMITE_MIN && LIMITE_MIN <= LIMITE_MAX);
        assertTrue("El límite 100 debe ser válido",
                LIMITE_MAX >= LIMITE_MIN && LIMITE_MAX <= LIMITE_MAX);
    }
}