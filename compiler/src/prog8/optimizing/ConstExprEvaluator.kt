package prog8.optimizing

import prog8.ast.*
import prog8.compiler.HeapValues
import kotlin.math.pow


val associativeOperators = setOf("+", "*", "&", "|", "^", "or", "and", "xor", "==", "!=")


class ConstExprEvaluator {

    fun evaluate(left: LiteralValue, operator: String, right: LiteralValue, heap: HeapValues): IExpression {
        return when(operator) {
            "+" -> plus(left, right, heap)
            "-" -> minus(left, right)
            "*" -> multiply(left, right, heap)
            "/" -> divide(left, right)
            "%" -> remainder(left, right)
            "**" -> power(left, right)
            "&" -> bitwiseand(left, right)
            "|" -> bitwiseor(left, right)
            "^" -> bitwisexor(left, right)
            "and" -> logicaland(left, right)
            "or" -> logicalor(left, right)
            "xor" -> logicalxor(left, right)
            "<" -> LiteralValue.fromBoolean(left < right, left.position)
            ">" -> LiteralValue.fromBoolean(left > right, left.position)
            "<=" -> LiteralValue.fromBoolean(left <= right, left.position)
            ">=" -> LiteralValue.fromBoolean(left >= right, left.position)
            "==" -> LiteralValue.fromBoolean(left == right, left.position)
            "!=" -> LiteralValue.fromBoolean(left != right, left.position)
            "<<" -> shiftedleft(left, right)
            ">>" -> shiftedright(left, right)
            else -> throw FatalAstException("const evaluation for invalid operator $operator")
        }
    }

    private fun shiftedright(left: LiteralValue, amount: LiteralValue): IExpression {
        if(left.asIntegerValue==null || amount.asIntegerValue==null)
            throw ExpressionError("cannot compute $left >> $amount", left.position)
        val result =
                if(left.type==DataType.UBYTE || left.type==DataType.UWORD)
                    left.asIntegerValue.ushr(amount.asIntegerValue)
                else
                    left.asIntegerValue.shr(amount.asIntegerValue)
        return LiteralValue.fromNumber(result, left.type, left.position)
    }

    private fun shiftedleft(left: LiteralValue, amount: LiteralValue): IExpression {
        if(left.asIntegerValue==null || amount.asIntegerValue==null)
            throw ExpressionError("cannot compute $left << $amount", left.position)
        val result = left.asIntegerValue.shl(amount.asIntegerValue)
        return LiteralValue.fromNumber(result, left.type, left.position)
    }

    private fun logicalxor(left: LiteralValue, right: LiteralValue): LiteralValue {
        val error = "cannot compute $left locical-bitxor $right"
        return when {
            left.asIntegerValue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.fromBoolean((left.asIntegerValue != 0) xor (right.asIntegerValue != 0), left.position)
                right.floatvalue!=null -> LiteralValue.fromBoolean((left.asIntegerValue != 0) xor (right.floatvalue != 0.0), left.position)
                else -> throw ExpressionError(error, left.position)
            }
            left.floatvalue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.fromBoolean((left.floatvalue != 0.0) xor (right.asIntegerValue != 0), left.position)
                right.floatvalue!=null -> LiteralValue.fromBoolean((left.floatvalue != 0.0) xor (right.floatvalue != 0.0), left.position)
                else -> throw ExpressionError(error, left.position)
            }
            else -> throw ExpressionError(error, left.position)
        }
    }

    private fun logicalor(left: LiteralValue, right: LiteralValue): LiteralValue {
        val error = "cannot compute $left locical-or $right"
        return when {
            left.asIntegerValue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.fromBoolean(left.asIntegerValue != 0 || right.asIntegerValue != 0, left.position)
                right.floatvalue!=null -> LiteralValue.fromBoolean(left.asIntegerValue != 0 || right.floatvalue != 0.0, left.position)
                else -> throw ExpressionError(error, left.position)
            }
            left.floatvalue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.fromBoolean(left.floatvalue != 0.0 || right.asIntegerValue != 0, left.position)
                right.floatvalue!=null -> LiteralValue.fromBoolean(left.floatvalue != 0.0 || right.floatvalue != 0.0, left.position)
                else -> throw ExpressionError(error, left.position)
            }
            else -> throw ExpressionError(error, left.position)
        }
    }

    private fun logicaland(left: LiteralValue, right: LiteralValue): LiteralValue {
        val error = "cannot compute $left locical-and $right"
        return when {
            left.asIntegerValue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.fromBoolean(left.asIntegerValue != 0 && right.asIntegerValue != 0, left.position)
                right.floatvalue!=null -> LiteralValue.fromBoolean(left.asIntegerValue != 0 && right.floatvalue != 0.0, left.position)
                else -> throw ExpressionError(error, left.position)
            }
            left.floatvalue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.fromBoolean(left.floatvalue != 0.0 && right.asIntegerValue != 0, left.position)
                right.floatvalue!=null -> LiteralValue.fromBoolean(left.floatvalue != 0.0 && right.floatvalue != 0.0, left.position)
                else -> throw ExpressionError(error, left.position)
            }
            else -> throw ExpressionError(error, left.position)
        }
    }

    private fun bitwisexor(left: LiteralValue, right: LiteralValue): LiteralValue {
        if(left.type== DataType.UBYTE) {
            if(right.asIntegerValue!=null) {
                return LiteralValue(DataType.UBYTE, bytevalue = (left.bytevalue!!.toInt() xor (right.asIntegerValue and 255)).toShort(), position = left.position)
            }
        } else if(left.type== DataType.UWORD) {
            if(right.asIntegerValue!=null) {
                return LiteralValue(DataType.UWORD, wordvalue = left.wordvalue!! xor right.asIntegerValue, position = left.position)
            }
        }
        throw ExpressionError("cannot calculate $left ^ $right", left.position)
    }

    private fun bitwiseor(left: LiteralValue, right: LiteralValue): LiteralValue {
        if(left.type== DataType.UBYTE) {
            if(right.asIntegerValue!=null) {
                return LiteralValue(DataType.UBYTE, bytevalue = (left.bytevalue!!.toInt() or (right.asIntegerValue and 255)).toShort(), position = left.position)
            }
        } else if(left.type== DataType.UWORD) {
            if(right.asIntegerValue!=null) {
                return LiteralValue(DataType.UWORD, wordvalue = left.wordvalue!! or right.asIntegerValue, position = left.position)
            }
        }
        throw ExpressionError("cannot calculate $left | $right", left.position)
    }

    private fun bitwiseand(left: LiteralValue, right: LiteralValue): LiteralValue {
        if(left.type== DataType.UBYTE) {
            if(right.asIntegerValue!=null) {
                return LiteralValue(DataType.UBYTE, bytevalue = (left.bytevalue!!.toInt() or (right.asIntegerValue and 255)).toShort(), position = left.position)
            }
        } else if(left.type== DataType.UWORD) {
            if(right.asIntegerValue!=null) {
                return LiteralValue(DataType.UWORD, wordvalue = left.wordvalue!! or right.asIntegerValue, position = left.position)
            }
        }
        throw ExpressionError("cannot calculate $left & $right", left.position)
    }

    private fun power(left: LiteralValue, right: LiteralValue): LiteralValue {
        val error = "cannot calculate $left ** $right"
        return when {
            left.asIntegerValue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.optimalNumeric(left.asIntegerValue.toDouble().pow(right.asIntegerValue), left.position)
                right.floatvalue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.asIntegerValue.toDouble().pow(right.floatvalue), position = left.position)
                else -> throw ExpressionError(error, left.position)
            }
            left.floatvalue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue.pow(right.asIntegerValue), position = left.position)
                right.floatvalue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue.pow(right.floatvalue), position = left.position)
                else -> throw ExpressionError(error, left.position)
            }
            else -> throw ExpressionError(error, left.position)
        }
    }

    private fun plus(left: LiteralValue, right: LiteralValue, heap: HeapValues): LiteralValue {
        val error = "cannot add $left and $right"
        return when {
            left.asIntegerValue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.optimalNumeric(left.asIntegerValue + right.asIntegerValue, left.position)
                right.floatvalue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.asIntegerValue + right.floatvalue, position = left.position)
                else -> throw ExpressionError(error, left.position)
            }
            left.floatvalue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue + right.asIntegerValue, position = left.position)
                right.floatvalue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue + right.floatvalue, position = left.position)
                else -> throw ExpressionError(error, left.position)
            }
            left.isString -> when {
                right.isString -> {
                    val newStr = left.strvalue(heap) + right.strvalue(heap)
                    if(newStr.length > 255) throw ExpressionError("string too long", left.position)
                    LiteralValue(DataType.STR, strvalue = newStr, position = left.position)
                }
                else -> throw ExpressionError(error, left.position)
            }
            else -> throw ExpressionError(error, left.position)
        }
    }

    private fun minus(left: LiteralValue, right: LiteralValue): LiteralValue {
        val error = "cannot subtract $left and $right"
        return when {
            left.asIntegerValue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.optimalNumeric(left.asIntegerValue - right.asIntegerValue, left.position)
                right.floatvalue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.asIntegerValue - right.floatvalue, position = left.position)
                else -> throw ExpressionError(error, left.position)
            }
            left.floatvalue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue - right.asIntegerValue, position = left.position)
                right.floatvalue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue - right.floatvalue, position = left.position)
                else -> throw ExpressionError(error, left.position)
            }
            else -> throw ExpressionError(error, left.position)
        }
    }

    private fun multiply(left: LiteralValue, right: LiteralValue, heap: HeapValues): LiteralValue {
        val error = "cannot multiply ${left.type} and ${right.type}"
        return when {
            left.asIntegerValue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue.optimalNumeric(left.asIntegerValue * right.asIntegerValue, left.position)
                right.floatvalue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.asIntegerValue * right.floatvalue, position = left.position)
                right.isString -> {
                    if(right.strvalue(heap).length * left.asIntegerValue > 255) throw ExpressionError("string too long", left.position)
                    LiteralValue(DataType.STR, strvalue = right.strvalue(heap).repeat(left.asIntegerValue), position = left.position)
                }
                else -> throw ExpressionError(error, left.position)
            }
            left.floatvalue!=null -> when {
                right.asIntegerValue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue * right.asIntegerValue, position = left.position)
                right.floatvalue!=null -> LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue * right.floatvalue, position = left.position)
                else -> throw ExpressionError(error, left.position)
            }
            else -> throw ExpressionError(error, left.position)
        }
    }

    private fun divideByZeroError(pos: Position): Unit =
            throw ExpressionError("division by zero", pos)

    private fun divide(left: LiteralValue, right: LiteralValue): LiteralValue {
        val error = "cannot divide $left by $right"
        return when {
            left.asIntegerValue!=null -> when {
                right.asIntegerValue!=null -> {
                    if(right.asIntegerValue==0) divideByZeroError(right.position)
                    val result: Int = left.asIntegerValue / right.asIntegerValue
                    LiteralValue.optimalNumeric(result, left.position)
                }
                right.floatvalue!=null -> {
                    if(right.floatvalue==0.0) divideByZeroError(right.position)
                    LiteralValue(DataType.FLOAT, floatvalue = left.asIntegerValue / right.floatvalue, position = left.position)
                }
                else -> throw ExpressionError(error, left.position)
            }
            left.floatvalue!=null -> when {
                right.asIntegerValue!=null -> {
                    if(right.asIntegerValue==0) divideByZeroError(right.position)
                    LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue / right.asIntegerValue, position = left.position)
                }
                right.floatvalue!=null -> {
                    if(right.floatvalue==0.0) divideByZeroError(right.position)
                    LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue / right.floatvalue, position = left.position)
                }
                else -> throw ExpressionError(error, left.position)
            }
            else -> throw ExpressionError(error, left.position)
        }
    }

    private fun remainder(left: LiteralValue, right: LiteralValue): LiteralValue {
        val error = "cannot compute remainder of $left by $right"
        return when {
            left.asIntegerValue!=null -> when {
                right.asIntegerValue!=null -> {
                    if(right.asIntegerValue==0) divideByZeroError(right.position)
                    LiteralValue.optimalNumeric(left.asIntegerValue.toDouble() % right.asIntegerValue.toDouble(), left.position)
                }
                right.floatvalue!=null -> {
                    if(right.floatvalue==0.0) divideByZeroError(right.position)
                    LiteralValue(DataType.FLOAT, floatvalue = left.asIntegerValue % right.floatvalue, position = left.position)
                }
                else -> throw ExpressionError(error, left.position)
            }
            left.floatvalue!=null -> when {
                right.asIntegerValue!=null -> {
                    if(right.asIntegerValue==0) divideByZeroError(right.position)
                    LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue % right.asIntegerValue, position = left.position)
                }
                right.floatvalue!=null -> {
                    if(right.floatvalue==0.0) divideByZeroError(right.position)
                    LiteralValue(DataType.FLOAT, floatvalue = left.floatvalue % right.floatvalue, position = left.position)
                }
                else -> throw ExpressionError(error, left.position)
            }
            else -> throw ExpressionError(error, left.position)
        }
    }
}
