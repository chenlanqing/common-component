package com.qing.fan.expression;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

/**
 * @author QingFan
 * @version 1.0.0
 * @date 2023年11月06日 21:16
 */
@Slf4j
public class SpringELParser {

    /**
     * thread-safe
     */
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * thread-safe
     */
    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    public String parse(Method method, Object[] arguments, String originParsableTarget) {
        if (!shouldParse(originParsableTarget)) {
            return originParsableTarget;
        }
        var context = new MethodBasedEvaluationContext(null, method, arguments, parameterNameDiscoverer);
        return doParse(originParsableTarget, context);
    }

    private String doParse(String originParsableTarget, MethodBasedEvaluationContext methodBasedEvaluationContext) {
        var attribute = "";
        try {
            var expression = expressionParser.parseExpression(originParsableTarget);
            attribute = expression.getValue(methodBasedEvaluationContext, String.class);
        } catch (ParseException | EvaluationException e) {
            log.warn("An error happened while parsing biz-target or biz-no.");
        }
        return attribute;
    }

    private boolean shouldParse(String originParsableTarget) {
        return StringUtils.isNotEmpty(originParsableTarget) && originParsableTarget.contains("#");
    }
}
