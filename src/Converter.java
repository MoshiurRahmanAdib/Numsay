import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Converter {

    private Converter() {}

    protected static StringBuilder convertInteger(short[] numberGroups, boolean hyphens, BigInteger illionOffset) { // illionOffset adds offsets to the illion index for custom scaling (e.g., starting at "million" instead of "thousand" for illionOffset of 1).
        if (numberGroups.length == 1 && numberGroups[0] == 0) return new StringBuilder("zero");

        StringBuilder converted = new StringBuilder();
        int numberOfLastGroupsWithoutIllion = illionOffset.compareTo(BigInteger.valueOf(2)) < 0 ? 2 - illionOffset.intValue() : 0;

        // Converts three digit groups (groups that are usually separated by comma when numbers are written)
        for (int i = 0; i < numberGroups.length - numberOfLastGroupsWithoutIllion; i++) {

            if (numberGroups[i] != 0) {
                converted.append(convertHundreds(numberGroups[i], hyphens)) // Adds the converted group of numbers
                        .append(" ")
                        .append(IllionGenerator.generateIllion(BigInteger.valueOf(numberGroups.length - i - 2).add(illionOffset)))
                        .append(" ");
            }

        }

        if (numberOfLastGroupsWithoutIllion >= 1) { // Adding the thousands and hundreds groups
            short thousandGroup = numberGroups.length - numberOfLastGroupsWithoutIllion < 0 ? 0 :
                    numberGroups[numberGroups.length - numberOfLastGroupsWithoutIllion];
            short hundredGroup = numberOfLastGroupsWithoutIllion == 2 ? numberGroups[numberGroups.length - 1] : 0;

            if (thousandGroup != 0) { // Adding the thousands group
                converted.append(convertHundreds(thousandGroup, hyphens)).append(" thousand");
                if (hundredGroup != 0) converted.append(" ");
            }
            if (hundredGroup != 0) { // Adding the hundreds group
                converted.append(convertHundreds(hundredGroup, hyphens));
            }

        }

        return converted;
    }

    protected static String convertDecimal(BigDecimal decimal) { // Convert the decimal/fraction part (digits after the decimal point)
        StringBuilder converted = new StringBuilder();

//        int scale = decimal.scale();
//        for (int i = 0; i < scale; i++) {
//            decimal = decimal.movePointRight(1);
//            converted.append(" ").append(TwoDigitNumbers.get(decimal.remainder(BigDecimal.TEN).intValue()));
//        }

        String plain = decimal.stripTrailingZeros().toPlainString();
        String fraction = plain.contains(".") ? plain.substring(plain.indexOf('.') + 1) : "";

        for (int i = 0; i < fraction.length(); i++) {
            converted.append(" ").append(TwoDigitNumbers.get(fraction.charAt(i) - '0'));
        }

        converted.deleteCharAt(0);
        return converted.toString();
    }

    private static String convertHundreds(short number, boolean hyphens) {
        StringBuilder converted = new StringBuilder();

        if (number >= 100) { // Hundreds
            converted.append(TwoDigitNumbers.get(number / 100, hyphens)).append(" hundred");
        }

        int tens = number % 100;
        if (tens != 0) { // Tens

            if (number >= 100) converted.append(" ");
            converted.append(TwoDigitNumbers.get(tens, hyphens));

        }

        return converted.toString();
    }

    // Used to generate illion with the using the Conway–Guy system
    private static class IllionGenerator {

        private IllionGenerator() {}

        // Primary illions up to nonillion (without the "on" as it is added later)
        private static final String[] ILLIONS = {"", "milli", "billi", "trilli", "quadrilli", "quintilli", "sextilli", "septilli", "octilli", "nonilli"};

        // Units, tens, and hundreds segments with their modifiers for constructing larger illions
        private static final String[] UNITS = {"", "un", "duo", "tre", "quattuor", "quin", "se", "septe", "octo", "nove"};
        private static final String[] TENS =         {"", "deci", "viginti", "triginta", "quadraginta", "quinquaginta", "sexaginta", "septuaginta", "octoginta", "nonaginta"};
        private static final String[] TENS_MODIFIER = {"",  "N",     "MS",       "NS",        "NS",           "NS",          "N",          "N",         "MX",         ""};
        private static final String[] HUNDREDS =         {"", "centi", "ducenti", "trecenti", "quadringenti", "quingenti", "sescenti", "septingenti", "octingenti", "nongenti"};
        private static final String[] HUNDREDS_MODIFIER = {"",  "NX",      "N",       "NS",         "NS",          "NS",        "N",         "N",          "MX",        ""};

        private static String generateIllion(BigInteger ordinal) { // Returns the ordinal-th illion, or 1000 * 1000^ordinal in text.
            StringBuilder converted = new StringBuilder();

            if (ordinal.compareTo(BigInteger.valueOf(1000)) < 0) {
                converted.append(generateIlli(ordinal.shortValue()));
            } else {
                short[] illiGroupsArray = getGroupsOfThousands(ordinal);

                for (short value : illiGroupsArray) {
                    converted.append(value == 0 ? "nilli" : generateIlli(value));
                }
            }

            return converted.append("on").toString();
        }

        // Generate the base illi for each power of 1000 (For example, milli (million without on)
        protected static String generateIlli(short ordinal) {

            if (ordinal <= 9) return ILLIONS[ordinal]; // Million - Nonillion

            int[] powerDigitsArray = {ordinal / 100, (ordinal % 100) / 10, ordinal % 10};
            StringBuilder converted = new StringBuilder();

            converted.append(UNITS[powerDigitsArray[2]]);

            // Sets the modifier depending on if the units is followed by tens or hundreds
            String modifier = powerDigitsArray[1] != 0 ? TENS_MODIFIER[powerDigitsArray[1]] : HUNDREDS_MODIFIER[powerDigitsArray[0]];

            if ((powerDigitsArray[2] == 3 || powerDigitsArray[2] == 6) && // Modify units based on the modifier conditions
                    (modifier.contains("S") || modifier.contains("X"))) {

                converted.append(powerDigitsArray[2] == 6 && modifier.contains("X") ? "x" : "s");

            } else if ((powerDigitsArray[2] == 7 || powerDigitsArray[2] == 9) &&
                    (modifier.contains("M") || modifier.contains("N"))) {

                converted.append(modifier.contains("M") ? "m" : "n");

            }

            converted.append(TENS[powerDigitsArray[1]]).append(HUNDREDS[powerDigitsArray[0]]);
            converted.deleteCharAt(converted.length() - 1).append("illi");

            return converted.toString();
        }
    }

    private static class TwoDigitNumbers {
        private static final Map<Integer, String> NUMBERS = new HashMap<>();
        private static final Map<Integer, String> NUMBERS_WITHOUT_DASH = new HashMap<>();

        private TwoDigitNumbers() {}

        static {
            String[] toTeens = {
                    "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
                    "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
            };
            String[] tens = {"zero", "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

            for (int i = 0; i < toTeens.length; i++) { // Put from 0 to 19
                NUMBERS.put(i, toTeens[i]);
            }

            for (int i = 2; i <= 9; i++) {
                String ten = tens[i];

                NUMBERS.put(i * 10, ten);

                for (int j = 1; j < 10; j++) {
                    int number = i * 10 + j;

                    NUMBERS.put(number, ten + "-" + toTeens[j]);
                    NUMBERS_WITHOUT_DASH.put(number, ten + " " + toTeens[j]);
                }
            }
        }

        public static String get(int number, boolean hyphens) {
//        if (number < 0 || number > 99) {
//            throw new IndexOutOfBoundsException("Number must be between 0 and 99. Parameter is " + number);
//        }

            return !hyphens && NUMBERS_WITHOUT_DASH.containsKey(number) ? NUMBERS_WITHOUT_DASH.get(number) : NUMBERS.get(number);
        }

        public static String get(int number) {
            return get(number, true);
        }

    }

    // Returns an array of the thousand groups (e.g. [12, 345, 678] for the number 12,345,678), grouping as ... millions, thousands, units
    protected static short[] getGroupsOfThousands(BigInteger number) {
        if (number.signum() == -1) number = number.abs();
        int length = number.toString().length();
        int groupCount = (length + 2) / 3;
        short[] groups = new short[groupCount];

        for (int i = groupCount - 1; i >= 0; i--) {
            groups[i] = number.remainder(BigInteger.valueOf(1000)).shortValue();
            number = number.divide(BigInteger.valueOf(1000));
        }

        return groups;
    }

}