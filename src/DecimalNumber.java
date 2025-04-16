import java.math.BigDecimal;
import java.math.BigInteger;

public class DecimalNumber implements Number {
    private final BigDecimal number;
    private final short[] integerPartArray;
    private final BigDecimal decimalPart;

    private final StringBuilder converted = new StringBuilder();

    public DecimalNumber(BigDecimal number, boolean hyphens) {
        this.number = number;
        BigInteger integerPart = number.toBigInteger();
        this.integerPartArray = Converter.getGroupsOfThousands(integerPart);
        this.decimalPart = number.remainder(BigDecimal.ONE).abs();

        if (number.signum() == -1) converted.append("negative ");

        converted.append(Converter.convertInteger(integerPartArray, hyphens, BigInteger.ZERO)); // Integer part conversion
        if (decimalPart.signum() != 0) converted.append(" point ").append(Converter.convertDecimal(decimalPart)); // Decimal part conversion
    }

    @Override
    public StringBuilder getConverted() {
        return converted;
    }

    @Override public String getConvertedString() {
        return converted.toString();
    }

    @Override
    public String[] getFormatted() {
        StringBuilder formatted = new StringBuilder();

        for (short value : integerPartArray) {
            formatted.append(",").append(value);
        }

        formatted.deleteCharAt(0);
        if (decimalPart.signum() != 0) formatted.append(".").append(decimalPart.toString().substring(2));

        if (number.signum() == -1) formatted.insert(0, "-");

        return new String[] {formatted.toString()};
    }

    @Override
    public String toString() {
        return number.toString();
    }
}
