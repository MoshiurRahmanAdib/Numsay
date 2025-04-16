import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Callable;

@Command(mixinStandardHelpOptions = true,
        version = "Numsay 0.1.0",
        description = "A CLI tool that spells out extremely large numbers using Conway and Guy's system.")
public class Main implements Callable<Integer> {
    @Parameters(index = "0", arity = "0..1", description = "The number to spell") String number;
    @Option(names = {"--no-hyphens", "-n"}, description = "Disable hyphens (for example, \"ninety nine\" instead of \"ninety-nine\")") boolean noHyphens;
    @Option(names = "-e", description = "Use scientific notation (provide exponent after -e)") String exponent;
    @Option(names = {"-s", "--show-number"}, description = "Format and show the number") boolean showNumber;
    @Option(names = {"-E", "--show-e-notation"}, description = "Show scientific numbers in E notation (-s and -e is required)") boolean eNotation;
    @Option(names = {"-t", "--execution-time"}, description = "Show execution time") boolean showExecutionTime;
    @Option(names = {"-f", "--file"}, description = "Read the number from a file (it will only read the first line and ignore the rest)") String filePath;
    @Option(names = {"-o", "--output"}, description = "Output the number to a file") String outputPath;
    @Option(names = {"-i", "--info"}, description = "Show more information about this program") boolean infoRequested;

    @Option(names = {"-V", "--version"}, versionHelp = true, description = "Print version information") boolean versionInfoRequested;
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help message") boolean usageHelpRequested;

    private static final CommandLine commandLine = new CommandLine(new Main());

    public static void main(String... args) {
        int exitCode = args.length != 0
                ? commandLine.execute(args)
                : repl();
        System.exit(exitCode);
    }

    @Override
    public Integer call() {

        if (infoRequested) {
            printInfo();
            return 0;
        }

        if (filePath == null && number == null) {
            throw new CommandLine.ParameterException(commandLine,
                    "Please provide a number or a file with -f, --file");
        } else if (filePath != null && number != null) {
            throw new CommandLine.ParameterException(commandLine,
                    "Please provide a number or a file with -f, --file, not both");
        } else if (filePath != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                number = reader.readLine(); // Reads the first line
                if (outputPath == null) System.err.println("Warning: You're reading the number from a file. If the output is too large for the terminal, use -o, --output <file> or redirect with '>' to save it to a file.\n");
            } catch (IOException e) {
                System.err.println("Error reading the file: " + e.getMessage());
                return 1;
            }
        }

        if (eNotation && (exponent == null || !showNumber)) {
            throw new CommandLine.ParameterException(commandLine,
                    "-E, --show-e-notation requires both --show-number and -e <exponent> options to be used.");
        }
        if (number.contains("e") || number.contains("E")) {
            throw new CommandLine.ParameterException(commandLine,
                    "Please use -e for scientific notation");
        }
        if (exponent != null && exponent.contains(".")) {
            throw new CommandLine.ParameterException(commandLine,
                    "Scientific notation exponents can only be integers");
        }

        long startTime = System.nanoTime();

        Number numberObject;
        try {
            numberObject = exponent == null
                    ? new DecimalNumber(new BigDecimal(number), !noHyphens)
                    : new ScientificNumber(new BigDecimal(number), new BigInteger(exponent), !noHyphens);
        } catch (NumberFormatException e) {
            throw new CommandLine.ParameterException(commandLine,
                    "Please enter a valid number");
        }

        long executionTime = (System.nanoTime() - startTime);

        if (showNumber) {
            System.out.print(numberObject.getFormatted()[eNotation ? 1 : 0] + ": ");
        }

        StringBuilder converted = numberObject.getConverted();
        converted.setCharAt(0, Character.toUpperCase(converted.charAt(0)));

        if (outputPath == null) {
            System.out.println(converted);
        } else {
            if (new File(outputPath).exists()) {
                System.out.println("The file already exists and will be overwritten, do you want to continue? (y/n)");

                while (true) {
                    String input = new Scanner(System.in).nextLine();
                    if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                        break;
                    } else if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                        return 0;
                    } else {
                        System.out.println("Please enter \"y\" or \"n\"");
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write(converted.toString());
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
                return 1;
            }
        }

        if (showExecutionTime) {
            System.out.printf("\nExecution time: %.3fms (%.6fs or %,dns)%n", executionTime / 1_000_000.0, executionTime / 1_000_000_000.0, executionTime);
        }

        return 0;
    }

    private static int repl() {
        Scanner scanner = new Scanner(System.in);

        commandLine.usage(System.out);
        System.out.println("\nEnter a blank line to exit.");

        while (true) {
            System.out.print("\n> ");
            String input;
            try {
                input = scanner.nextLine();
            } catch (NoSuchElementException e) {
                return 0;
            }

            if (input.isBlank()) return 0;

            String[] args = input.trim().split("\\s+");
            int exitCode = commandLine.execute(args);

            if (exitCode != 0) {
                System.out.println("\nPress enter to exit, or type \"continue\" to continue...");
                try {
                    if (!scanner.nextLine().trim().equalsIgnoreCase("continue")) return exitCode;
                } catch (NoSuchElementException e) {
                    return 0;
                }
            }
        }

    }

    private void printInfo() {
        System.out.println("""
                Numsay - A CLI tool that spells out extremely large numbers using the Conway-Guy naming system.
                
                This naming system was introduced by John Horton Conway and Richard K. Guy
                in The Book of Numbers (1996).
                
                Created by Moshiur Rahman Adib :)
                GitHub: https://github.com/MoshiurRahmanAdib/Numsay
                License: Apache License 2.0
                Copyright(c) 2025 Moshiur Rahman Adib""");

        System.out.print("\nVersion: "); commandLine.printVersionHelp(System.out);
        System.out.println("Java 17 or newer is required.\n");
        commandLine.usage(System.out);
    }

}