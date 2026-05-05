package org.modogthedev.superposition.system.card.actions;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import net.minecraft.resources.ResourceLocation;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.core.SuperpositionActions;
import org.modogthedev.superposition.system.card.Action;
import org.modogthedev.superposition.system.card.BiModifyAction;
import org.modogthedev.superposition.system.card.actions.configuration.StringConfiguration;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.data.EncodedData;

public class ArithmeticAction extends Action implements BiModifyAction {

    public ArithmeticAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public Signal modify(Signal firstSignal, Signal secondSignal) {
        if (getConfigurations().getFirst() instanceof StringConfiguration configuration) {
            String string = configuration.getString();
            Expression expression = new Expression(string, Superposition.configuration);
            if (firstSignal != null && firstSignal.getEncodedData() != null && secondSignal != null && secondSignal.getEncodedData() != null) {
                firstSignal.getEncodedData().asExpressionVariable("a",expression);
                secondSignal.getEncodedData().asExpressionVariable("b",expression);

                try {
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
