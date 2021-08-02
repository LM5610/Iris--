/*
 * Iris is a World Generator for Minecraft Bukkit Servers
 * Copyright (c) 2021 Arcane Arts (Volmit Software)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.volmit.iris.engine.object;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Scope;
import com.volmit.iris.Iris;
import com.volmit.iris.engine.IrisComplex;
import com.volmit.iris.engine.IrisEngine;
import com.volmit.iris.engine.cache.AtomicCache;
import com.volmit.iris.engine.framework.Engine;
import com.volmit.iris.engine.object.annotations.*;
import com.volmit.iris.engine.object.common.IRare;
import com.volmit.iris.engine.stream.ProceduralStream;
import com.volmit.iris.util.collection.KList;
import com.volmit.iris.util.math.RNG;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@SuppressWarnings("DefaultAnnotationParam")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Desc("Represents Block Data")
@Data
@EqualsAndHashCode(callSuper = false)
public class IrisExpressionGenerator extends IrisRegistrant {
    private static final Parser parser = new Parser();

    @ArrayType(type = IrisExpressionGenerator.class, min = 1)
    @Desc("Variables to use in this expression")
    private KList<IrisExpressionLoad> variables = new KList<>();

    @Required
    @Desc("The expression. Inherited variables are x, y and z. Avoid using those variable names.")
    private String expression;

    private transient AtomicCache<Expression> expressionCache = new AtomicCache<>();

    private Expression expression()
    {
        return expressionCache.aquire(() -> {
            Scope scope = new Scope(); // Create variable scope. This scope can hold both constants and invocation variables.

            try
            {
                for(IrisExpressionLoad i : variables)
                {
                    scope.addInvocationVariable(i.getName());
                }

                scope.addInvocationVariable("x");
                scope.addInvocationVariable("y");
                scope.addInvocationVariable("z");
            }

            catch(Throwable e)
            {
                e.printStackTrace();
                Iris.error("Script Variable load error in " + getLoadFile().getPath());
            }

            try
            {
                return parser.parse(getExpression(), scope);
            }

            catch(Throwable e)
            {
                e.printStackTrace();
                Iris.error("Script load error in " + getLoadFile().getPath());
            }

            return null;
        });
    }

    public double evaluate(RNG rng, double x, double z)
    {
        double[] g = new double[3 + getVariables().size()];
        int m = 0;
        for(IrisExpressionLoad i : getVariables())
        {
            g[m++] = i.getValue(rng, x, z);
        }

        g[m++] = x;
        g[m++] = -1;
        g[m] = z;

        return expression().evaluate(g);
    }

    public double evaluate(RNG rng, double x, double y, double z)
    {
        double[] g = new double[3 + getVariables().size()];
        int m = 0;
        for(IrisExpressionLoad i : getVariables())
        {
            g[m++] = i.getValue(rng, x, y, z);
        }

        g[m++] = x;
        g[m++] = y;
        g[m] = z;

        return expression().evaluate(g);
    }
}