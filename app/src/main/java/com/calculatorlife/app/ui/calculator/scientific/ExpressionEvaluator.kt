package com.calculatorlife.app.ui.calculator.scientific

import kotlin.math.*

class ExpressionException(message: String) : Exception(message)

enum class AngleMode { DEGREES, RADIANS }

/**
 * Precedence-aware expression evaluator for the Scientific calculator.
 * Supports +, -, *, /, ^, %, unary minus, parentheses, and the functions
 * sin/cos/tan/asin/acos/atan/log/ln/sqrt/exp plus constants pi/e.
 *
 * Pure Kotlin, no Android dependency — directly unit-testable.
 * Grammar:
 *   expression := term (('+' | '-') term)*
 *   term       := unary (('*' | '/') unary)*
 *   unary      := '-' unary | power
 *   power      := postfix ('^' unary)?        // right-associative
 *   postfix    := primary '%'?
 *   primary    := NUMBER | IDENT '(' expression ')' | IDENT | '(' expression ')'
 */
class ExpressionEvaluator(private val angleMode: AngleMode = AngleMode.DEGREES) {

    fun evaluate(expression: String): Double {
        val tokens = tokenize(expression)
        if (tokens.isEmpty()) throw ExpressionException("Empty expression")
        val parser = Parser(tokens)
        val result = parser.parseExpression()
        if (!parser.isAtEnd()) throw ExpressionException("Unexpected token")
        if (result.isNaN() || result.isInfinite()) throw ExpressionException("Math error")
        return result
    }

    private sealed class Token {
        data class Num(val value: Double) : Token()
        data class Ident(val name: String) : Token()
        data class Sym(val char: Char) : Token()
    }

    private fun tokenize(expression: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        while (i < expression.length) {
            val c = expression[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val start = i
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) i++
                    tokens += Token.Num(expression.substring(start, i).toDouble())
                }
                c.isLetter() -> {
                    val start = i
                    while (i < expression.length && expression[i].isLetter()) i++
                    tokens += Token.Ident(expression.substring(start, i))
                }
                c in "+-*/^%()" -> { tokens += Token.Sym(c); i++ }
                else -> throw ExpressionException("Unexpected character: $c")
            }
        }
        return tokens
    }

    private inner class Parser(private val tokens: List<Token>) {
        private var pos = 0
        fun isAtEnd() = pos >= tokens.size
        private fun peek(): Token? = tokens.getOrNull(pos)
        private fun advance(): Token = tokens[pos++]

        fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                val t = peek()
                if (t is Token.Sym && (t.char == '+' || t.char == '-')) {
                    advance()
                    val rhs = parseTerm()
                    value = if (t.char == '+') value + rhs else value - rhs
                } else break
            }
            return value
        }

        private fun parseTerm(): Double {
            var value = parseUnary()
            while (true) {
                val t = peek()
                if (t is Token.Sym && (t.char == '*' || t.char == '/')) {
                    advance()
                    val rhs = parseUnary()
                    if (t.char == '/' && rhs == 0.0) throw ExpressionException("Cannot divide by zero")
                    value = if (t.char == '*') value * rhs else value / rhs
                } else break
            }
            return value
        }

        private fun parseUnary(): Double {
            val t = peek()
            if (t is Token.Sym && t.char == '-') {
                advance()
                return -parseUnary()
            }
            return parsePower()
        }

        private fun parsePower(): Double {
            val base = parsePostfix()
            val t = peek()
            if (t is Token.Sym && t.char == '^') {
                advance()
                val exponent = parseUnary()
                return base.pow(exponent)
            }
            return base
        }

        private fun parsePostfix(): Double {
            var value = parsePrimary()
            while (true) {
                val t = peek()
                if (t is Token.Sym && t.char == '%') {
                    advance()
                    value /= 100.0
                } else break
            }
            return value
        }

        private fun parsePrimary(): Double {
            val t = peek() ?: throw ExpressionException("Unexpected end of expression")
            return when (t) {
                is Token.Num -> { advance(); t.value }
                is Token.Sym -> {
                    if (t.char == '(') {
                        advance()
                        val value = parseExpression()
                        val close = peek()
                        if (close !is Token.Sym || close.char != ')') throw ExpressionException("Missing ')'")
                        advance()
                        value
                    } else throw ExpressionException("Unexpected symbol: ${t.char}")
                }
                is Token.Ident -> {
                    advance()
                    when (t.name.lowercase()) {
                        "pi" -> PI
                        "e" -> E
                        else -> {
                            val open = peek()
                            if (open is Token.Sym && open.char == '(') {
                                advance()
                                val arg = parseExpression()
                                val close = peek()
                                if (close !is Token.Sym || close.char != ')') throw ExpressionException("Missing ')'")
                                advance()
                                applyFunction(t.name.lowercase(), arg)
                            } else throw ExpressionException("Unknown identifier: ${t.name}")
                        }
                    }
                }
            }
        }
    }

    private fun applyFunction(name: String, arg: Double): Double {
        val radians = if (angleMode == AngleMode.DEGREES) Math.toRadians(arg) else arg
        return when (name) {
            "sin" -> sin(radians)
            "cos" -> cos(radians)
            "tan" -> tan(radians)
            "asin" -> radiansResultToDisplay(asin(arg))
            "acos" -> radiansResultToDisplay(acos(arg))
            "atan" -> radiansResultToDisplay(atan(arg))
            "log" -> { if (arg <= 0) throw ExpressionException("log of non-positive number"); log10(arg) }
            "ln" -> { if (arg <= 0) throw ExpressionException("ln of non-positive number"); ln(arg) }
            "sqrt" -> { if (arg < 0) throw ExpressionException("sqrt of negative number"); sqrt(arg) }
            "exp" -> exp(arg)
            "abs" -> abs(arg)
            else -> throw ExpressionException("Unknown function: $name")
        }
    }

    private fun radiansResultToDisplay(radiansResult: Double): Double =
        if (angleMode == AngleMode.DEGREES) Math.toDegrees(radiansResult) else radiansResult
}
