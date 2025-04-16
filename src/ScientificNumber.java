import java.math.BigDecimal;
import java.math.BigInteger;

public class ScientificNumber implements Number {
    private final StringBuilder converted = new StringBuilder();
    private final BigDecimal coefficient;
    private final BigInteger exponent;

    public ScientificNumber(BigDecimal coefficient, BigInteger exponent, boolean dashes) {
        this.coefficient = coefficient;
        this.exponent = exponent;

        if (coefficient.signum() == -1) converted.append("negative ");

        BigDecimal number = null;

        if (exponent.signum() <= 0) {
            number = coefficient.movePointLeft(exponent.abs().intValueExact());

        } else if (exponent.intValue() <= coefficient.scale() && exponent.intValue() >= 0) {
            number = coefficient.movePointRight(exponent.intValue());

        } else { // If exponent is large enough for illionOffset
            int moveDecimalPointAmount = coefficient.scale();
            exponent = exponent.subtract(BigInteger.valueOf(moveDecimalPointAmount));

            int extra = exponent.mod(BigInteger.valueOf(3)).intValue();
            moveDecimalPointAmount += extra;
            exponent = exponent.subtract(BigInteger.valueOf(extra));

            converted.append(Converter.convertInteger(
                    Converter.getGroupsOfThousands(
                            coefficient.movePointRight(moveDecimalPointAmount)
                                    .toBigIntegerExact()
                    ),
                    dashes,
                    exponent.divide(BigInteger.valueOf(3))
            ));

        }

        if (number != null) { // If no illionOffset is done
            converted.append(Converter.convertInteger( // Integer part conversion
                    Converter.getGroupsOfThousands(number.toBigInteger()),
                    dashes, BigInteger.ZERO));

            BigDecimal decimalPart = number.remainder(BigDecimal.ONE).abs();
            if (decimalPart.signum() != 0)
                converted.append(" point ").append(Converter.convertDecimal(decimalPart)); // Decimal part conversion
        }

    }

    @Override
    public StringBuilder getConverted() {
        return converted;
    }

    @Override
    public String[] getFormatted() {
        return new String[] {coefficient + " * 10^" + exponent,
                coefficient + "e" + exponent};
    }

    @Override
    public String getConvertedString() {
        return converted.toString();
    }
}
