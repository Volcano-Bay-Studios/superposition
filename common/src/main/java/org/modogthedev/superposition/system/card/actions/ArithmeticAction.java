package org.modogthedev.superposition.system.card.actions;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.ArraySetExecutableAction;
import org.modogthedev.superposition.system.card.actions.configuration.StringConfiguration;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;

import java.util.List;

public class ArithmeticAction extends ArraySetExecutableAction {
    //TODO: Make this more similar to the constant combinator

    public ArithmeticAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    protected List<String> arrayKeys() {
        return List.of("a","b");
    }

    @Override
    protected Signal executeArray(Signal[] signals) {
        Signal firstSignal = signals[0];
        Signal secondSignal = signals[1];
        if (getConfigurations().getFirst() instanceof StringConfiguration configuration) {
            String string = configuration.getString();
            Expression expression = new Expression(string, Superposition.configuration);
            if (firstSignal != null && firstSignal.getEncodedData() != null && secondSignal != null && secondSignal.getEncodedData() != null) {

                try {
                    firstSignal.getEncodedData().asExpressionVariable("a",expression);
                    secondSignal.getEncodedData().asExpressionVariable("b",expression);
                    EvaluationValue evaluation = expression.evaluate();
                    float value = Float.parseFloat(evaluation.getStringValue());
                    firstSignal.setEncodedData(EncodedData.of(value));
                } catch (Exception ignored) {
                }
            }
        }
        return firstSignal;

    }

    @Override
    protected void setupConfigurations() {
        getConfigurations().add(SuperpositionActions.EXPRESSION_CONFIGURATION.get().copy());
    }
}
