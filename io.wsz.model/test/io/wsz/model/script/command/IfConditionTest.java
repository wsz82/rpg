package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.Model;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.script.ScriptValidator;
import io.wsz.model.script.variable.VariableBoolean;
import io.wsz.model.script.variable.Variables;
import io.wsz.model.world.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IfConditionTest {
    private static ScriptValidator validator;
    private static Controller controller;

    private final String case1 = "!(G.first)";
    private final String case2 = "(!G.first)";
    private final String case3 = "G.firstAND!G.second";
    private final String case4 = "(G.firstOR!G.second)";
    private final String case5 = "!(G.firstORG.second)ANDG.third";
    private final String case6 = "(G.firstORG.second)AND!G.third";
    private final String case7 = "(!(G.firstORG.second)AND!(G.thirdANDG.fourth))";
    private final String case8 = "(!(G.firstORG.secondAND!(G.thirdORG.fourth)))";

    @BeforeAll
    static void setUpController() {
        controller = new Controller();
        Model model = new Model();
        Plugin plugin = new Plugin();
        World world = new World();
        Variables variables = new Variables(true);
        List<VariableBoolean> booleans = variables.getBooleans();
        VariableBoolean first = new VariableBoolean("first", true);
        VariableBoolean second = new VariableBoolean("second", true);
        VariableBoolean third = new VariableBoolean("third", true);
        VariableBoolean fourth = new VariableBoolean("fourth", true);
        booleans.add(first);
        booleans.add(second);
        booleans.add(third);
        booleans.add(fourth);
        world.setVariables(variables);
        plugin.setWorld(world);
        model.setActivePlugin(plugin);
        controller.setModel(model);
        validator = new ScriptValidator(controller);
    }

    @Test
    public void isNegateSet1() {
        IfCondition ifCondition = getIfCondition(case1);
        assertTrue(ifCondition.isNegate());
    }

    @Test
    public void isExpressionSet1() {
        IfCondition ifCondition = getIfCondition(case1);
        assertNull(ifCondition.getExpression());
        assertNotNull(ifCondition.getInnerCondition().getExpression());
    }

    @Test
    public void isNegateSet2() {
        IfCondition ifCondition = getIfCondition(case2);
        IfCondition innerCondition = ifCondition.getInnerCondition();
        assertTrue(innerCondition.isNegate());
    }

    @Test
    public void isNegateSet3() {
        IfCondition ifCondition = getIfCondition(case3);
        assertTrue(ifCondition.getAndOrIfCondition().isNegate());
    }

    @Test
    public void isExpressionSet3() {
        IfCondition ifCondition = getIfCondition(case3);
        assertNotNull(ifCondition.getExpression());
        assertNull(ifCondition.getInnerCondition());
        assertNotNull(ifCondition.getAndOrIfCondition().getExpression());
    }

    @Test
    public void isNegateSet4() {
        IfCondition ifCondition = getIfCondition(case4);
        IfCondition innerCondition = ifCondition.getInnerCondition();
        assertTrue(innerCondition.getAndOrIfCondition().isNegate());
    }

    @Test
    public void isExpressionSet4() {
        IfCondition ifCondition = getIfCondition(case4);
        assertNull(ifCondition.getExpression());
        assertNull(ifCondition.getAndOrIfCondition());
        IfCondition innerCondition = ifCondition.getInnerCondition();
        assertNotNull(innerCondition);
        IfCondition andOrIfCondition = innerCondition.getAndOrIfCondition();
        assertNotNull(andOrIfCondition);
        assertNotNull(andOrIfCondition.getExpression());
    }

    @Test
    public void isNegateSet5() {
        IfCondition ifCondition = getIfCondition(case5);
        assertTrue(ifCondition.isNegate());
    }

    @Test
    public void isExpressionSet5() {
        IfCondition ifCondition = getIfCondition(case5);
        IfCondition andOrIfCondition = ifCondition.getAndOrIfCondition();
        assertNotNull(andOrIfCondition);
        assertNotNull(andOrIfCondition.getExpression());
    }

    @Test
    public void isNegateSet6() {
        IfCondition ifCondition = getIfCondition(case6);
        assertTrue(ifCondition.getAndOrIfCondition().isNegate());
    }

    @Test
    public void isExpressionSet6() {
        IfCondition ifCondition = getIfCondition(case6);
        IfCondition andOrIfCondition = ifCondition.getAndOrIfCondition();
        assertNotNull(andOrIfCondition);
        assertNotNull(andOrIfCondition.getExpression());
    }

    @Test
    public void isNegateSet7() {
        IfCondition ifCondition = getIfCondition(case7);
        IfCondition innerCondition = ifCondition.getInnerCondition();
        assertTrue(innerCondition.getAndOrIfCondition().isNegate());
    }

    @Test
    public void isExpressionSet7() {
        IfCondition mainBrackets = getIfCondition(case7);

        assertNotNull(mainBrackets);
        assertNull(mainBrackets.getExpression());
        assertNull(mainBrackets.getAndOrIfCondition());

        IfCondition G_first_OR_G_second = mainBrackets.getInnerCondition();
        assertNotNull(G_first_OR_G_second);
        assertNull(G_first_OR_G_second.getExpression());

        IfCondition G_first_OR = G_first_OR_G_second.getInnerCondition();
        assertNotNull(G_first_OR);
        assertNotNull(G_first_OR.getExpression());

        IfCondition G_second = G_first_OR.getAndOrIfCondition();
        assertNotNull(G_second);
        assertNotNull(G_second.getExpression());

        IfCondition G_third_AND_G_fourth = G_first_OR_G_second.getAndOrIfCondition();
        assertNotNull(G_third_AND_G_fourth);
        assertNull(G_third_AND_G_fourth.getExpression());
        assertNull(G_third_AND_G_fourth.getAndOrIfCondition());

        IfCondition G_third = G_third_AND_G_fourth.getInnerCondition();
        assertNotNull(G_third);
        assertNotNull(G_third.getExpression());

        IfCondition G_fourth = G_third.getAndOrIfCondition();
        assertNotNull(G_fourth);
        assertNotNull(G_fourth.getExpression());
    }

    @Test
    public void isNegateSet8() {
        IfCondition mainBrackets = getIfCondition(case8);
        IfCondition innerBrackets = mainBrackets.getInnerCondition();
        assertTrue(innerBrackets.getInnerCondition().getAndOrIfCondition().getAndOrIfCondition().isNegate());
    }

    @Test
    public void isExpressionSet8() {
        IfCondition mainBrackets = getIfCondition(case8);
        IfCondition innerBrackets = mainBrackets.getInnerCondition();
        IfCondition thirdCondition = innerBrackets.getInnerCondition().getAndOrIfCondition().getAndOrIfCondition();
        assertNull(thirdCondition.getExpression());
        IfCondition G_third = thirdCondition.getInnerCondition();
        assertNotNull(G_third);
        assertNotNull(G_third.getExpression());
        assertNotNull(G_third.getAndOrIfCondition());
    }

    private IfCondition getIfCondition(String case3) {
        return IfCondition.parseIfCondition("", case3, controller, validator);
    }
}