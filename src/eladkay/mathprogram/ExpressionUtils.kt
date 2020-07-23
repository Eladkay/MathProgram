package eladkay.mathprogram

object ExpressionUtils {
    private val SIN_REGEX = "sin\\((.*)\\)".toRegex()
    private val COS_REGEX = "cos\\((.*)\\)".toRegex()
    fun evaluate(string: String, value: Double, variable: String = "x"): Double {
        //val toParse = string.replace("π", Math.PI.toString())
        val toParse = string.replace("(-?\\d*\\.?\\d*)π".toRegex()) {
            val number = it.groupValues[1]
            if(number.isBlank() || number.toDoubleOrNull() == null) Math.PI.toString()
            else (number.toDouble() * Math.PI).toString()
        }
        var ret = 0.0
        if(isPolynomial(toParse, variable)) ret = evaluatePolynomial(toParse, value, variable)
        else if("+" in toParse)
            ret = toParse.split("+").map { evaluate(it.trim(), value, variable) }.sum()
        else if(toParse.matches(SIN_REGEX) && isPolynomial(SIN_REGEX.matchEntire(toParse)!!.groups[1]!!.value, variable))
            ret = Math.sin(evaluatePolynomial(SIN_REGEX.matchEntire(toParse)!!.groups[1]!!.value, value, variable))
        else if(toParse.matches(COS_REGEX) && isPolynomial(COS_REGEX.matchEntire(toParse)!!.groups[1]!!.value, variable))
            ret = Math.cos(evaluatePolynomial(COS_REGEX.matchEntire(toParse)!!.groups[1]!!.value, value, variable))
        else if("/" in toParse)
            ret = evaluate(toParse.split("/")[0], value, variable) / evaluate(toParse.split("/")[1], value, variable)
        else if("*" in toParse)
            ret = evaluate(toParse.split("*")[0], value, variable) * evaluate(toParse.split("*")[1], value, variable)
        else if("^" in toParse)
            ret = Math.pow(evaluate(toParse.split("^")[0], value, variable), evaluate(toParse.split("^")[1], value, variable))
        return ret //if(Math.abs(ret) < Math.pow(10.0, -10.0)) 0.0 else ret
    }
    fun limit(string: String, value: Double, variable: String = "x"): Double
            = (evaluate(string, value + Math.pow(10.0, -6.0), variable) + evaluate(string, value - Math.pow(10.0, -6.0), variable))/2
    fun approximateRoot(string: String, startingValue: Double = 0.0, variable: String = "x"): Double {
        var x = startingValue
        fun g(x: Double): Double {
            val h = evaluate(string, x, variable)
            val fxh = evaluate(string, x + h, variable)
            return -1 + fxh / h
        }
        // Steffensen's method
        for(i in 1..100) {
            if(evaluate(string, x, variable) == 0.0 || g(x) == 0.0) return x
            x -= evaluate(string, x, variable) / g(x)
        }
        return x
    }
    private fun evaluatePolynomial(string: String, value: Double, variable: String = "x"): Double {
        if(!isPolynomial(string, variable)) throw IllegalArgumentException()
        return string.replace("-", "+-").split("+").map {
            @Suppress("NAME_SHADOWING")
            val it = it.replace("-$variable", "-1$variable").replace(" ", "")
            if(variable in it) {
                if("^" in it) {
                    if(it.startsWith(variable)) listOf(1.0, it.split("$variable^")[1].toDouble())
                    else listOf(it.split("$variable^")[0].toDouble(), it.split("$variable^")[1].toDouble())
                } else if(it.startsWith(variable)) listOf(1.0, 1.0) else listOf(it.split(variable)[0].toDouble(), 1.0)
            } else listOf(it.toDouble(), 0.0)
        }.map { it[0] * Math.pow(value, it[1]) }.sum()
    }
    fun isPolynomial(string: String, variable: String = "x"): Boolean {
        return string.replace("-", "+-").replace("-$variable", "-1$variable").split("+").map {
            val terms = it.trim().replace(" ", "").split(variable).filter { it.isNotBlank() }.map { it.removePrefix("^").removeSuffix("^") }
            //println(terms)
            (terms.size == 2 && (terms[0].toDoubleOrNull() != null || terms[0] == "") && (terms[1].removeSuffix("^").toIntOrNull() != null || terms[1] == ""))
            || (terms.size == 1 && (terms[0].removeSurrounding("^").toDoubleOrNull() != null || terms[0] == "" || terms[0] == variable))
            || terms.isEmpty()
        }.all { it }
    }

    // p: ∀ε∃δ∀x: 0<|x-x0|<δ → |f(x)-L|<ε
    // not p: ∃ε∀δ∃x: -(0<|x-x0|<δ → |f(x)-L|<ε)
    private val VARIABLE_EXPRESSION_REGEX = "[∀∃][a-zA-Zα-ωΑ-Ω\\s](?::?)".toRegex()
    fun negate(expression: String, prefix: String = ""): String { // todo!
        var newPref = prefix
        val indexAll = expression.indexOf("∀")
        val indexExists = expression.indexOf("∃")
        val index: Int
        when {
            indexAll > indexExists && indexExists != -1 -> {
                // exists first
                index = indexExists
//                val localSubstring = expression.substring(indexAll, indexAll + 2) 🤔
//                if(localSubstring.matches(VARIABLE_EXPRESSION_REGEX))
//                    index += 2
                newPref += if(index > 0)
                    expression.substring(0 until index) + "∀"
                else "∀"
                val newExpression = expression.substring(index + 1)
                return negate(newExpression, newPref)
            }
            indexAll != -1 -> {
                // all first
                index = indexAll
//                val localSubstring = expression.substring(indexAll, indexAll + 2)  🤔
//                if(localSubstring.matches(VARIABLE_EXPRESSION_REGEX))
//                    index += 2
                newPref += if(index > 0)
                    expression.substring(0 until index) + "∃"
                else "∃"
                val newExpression = expression.substring(index + 1)
                return negate(newExpression, newPref)
            }
            else -> // none of both
                return "$prefix -($expression)"
        }
    }
}