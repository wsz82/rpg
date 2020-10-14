package io.wsz.model.script.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutableTest {

    @Test
    void getBlockCloseIndex1() {
        If instruction = new If();
        String blockOpen = "if(boo){x}";
        int closeIndex = instruction.getBlockCloseIndex(blockOpen);
        assertEquals(blockOpen.length() - 1, closeIndex);
    }

    @Test
    void getBlockCloseIndex2() {
        If instruction = new If();
        String blockOpen = "if(boo){x; if(foo){y}}";
        int closeIndex = instruction.getBlockCloseIndex(blockOpen);
        assertEquals(blockOpen.length() - 1, closeIndex);
    }

    @Test
    void getBlockCloseIndex3() {
        If instruction = new If();
        String blockOpen = "if(foo){y}}";
        int closeIndex = instruction.getBlockCloseIndex(blockOpen);
        assertEquals(blockOpen.length() - 2, closeIndex);
    }

    @Test
    void getBlockCloseIndex4() {
        If instruciton = new If();
        String blockOpen = "if(foo){y}elseif(boo){z}}";
        int closeIndex = instruciton.getBlockCloseIndex(blockOpen);
        assertEquals(9, closeIndex);
    }

    @Test
    void getBlockCloseIndex5() {
        If instruction = new If();
        String blockOpen = "{}}}";
        int closeIndex = instruction.getBlockCloseIndex(blockOpen);
        assertEquals(1, closeIndex);
    }

    @Test
    void getBlockCloseIndex6() {
        If instruction = new If();
        String blockOpen = "{{}}}}";
        int closeIndex = instruction.getBlockCloseIndex(blockOpen);
        assertEquals(3, closeIndex);
    }

    @Test
    void getBlockCloseIndex7() {
        If instruction = new If();
        String blockOpen = "{{{}}}}";
        int closeIndex = instruction.getBlockCloseIndex(blockOpen);
        assertEquals(5, closeIndex);
    }
}