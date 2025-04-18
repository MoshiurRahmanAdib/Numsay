<p align="center">
  <img src="resources/Icon.svg" width="128">
</p>
<h1 align="center">Numsay</h1>

A CLI tool that spells out extremely large numbers using the Conway-Guy naming system. This naming system was introduced by [John Horton Conway](https://en.wikipedia.org/wiki/John_Horton_Conway) and [Richard K. Guy](https://en.wikipedia.org/wiki/Richard_K._Guy) in [The Book of Numbers](https://en.wikipedia.org/wiki/The_Book_of_Numbers_(math_book)).

```
> 340282366920938463463374607431768211456 -st

340,282,366,920,938,463,463,374,607,431,768,211,456: Three hundred forty undecillion two hundred eighty-two decillion three hundred sixty-six nonillion nine hundred twenty octillion nine hundred thirty-eight septillion four hundred sixty-three sextillion four hundred sixty-three quintillion three hundred seventy-four quadrillion six hundred seven trillion four hundred thirty-one billion seven hundred sixty-eight million two hundred eleven thousand four hundred fifty-six

Execution time: 0.233ms (0.000233s or 233,300ns)
```

```
> 123456790 -ste 1000000

123456790 * 10^1000000: One trestrigintatrecentilliquintrigintatrecentillion two hundred thirty-four trestrigintatrecentilliquattuortrigintatrecentillion five hundred sixty-seven trestrigintatrecentillitrestrigintatrecentillion nine hundred trestrigintatrecentilliduotrigintatrecentillion

Execution time: 0.146ms (0.000146s or 145,500ns)
```

## Usage

> ⚠️ Numsay requires Java 17 or newer.
> 
> You can download it here: https://www.oracle.com/java/technologies/downloads/

Go to the [releases](https://github.com/MoshiurRahmanAdib/Numsay/releases) page and download the latest JAR, or EXE if you're on Windows if you want.

To use it, you can do something like this:

```bash
java -jar Numsay.jar 123
```

Or if you're using the exe:

```powershell
./Numsay.exe 123
```

More usage options are shown below. Make sure you have Java installed, otherwise it won't run. If you want, you can add it to your PATH so you can run `numsay` from anywhere.

### Options
```
Usage: numsay [-sEnthVi] [-e=<exponent>] [-f=<filePath>] [-o=<outputPath>] [<number>]

      [<number>]              The number to spell
  -e=<exponent>               Use scientific notation (provide exponent after -e)
  -s, --show-number           Format and show the number
  -E, --show-e-notation       Show scientific numbers in E notation (-s and -e is required)
  -n, --no-hyphens            Disable hyphens (for example, "ninety nine" instead of "ninety-nine")
  -f, --file=<filePath>       Read the number from a file (it will only read the first line and ignore the rest)
  -o, --output=<outputPath>   Output the number to a file
  -t, --execution-time        Show execution time
  -h, --help                  Show this help message
  -V, --version               Print version information
  -i, --info                  Show more information about this program
```
### Display and Formatting Options
- `-s`, `--show-number`: Shows the number it got as input. Example: `123: One hundred twenty-three`.
- `-E`, `--show-e-notation`: When showing the number with `-s` and using scientific notation (see below), it will show the number in E notation. Example: `1.23e10` instead of `1.23 * 10^10`.
- `-n`, `--no-hyphens`: Disable hyphens. Example: `ninety nine` instead of `ninety-nine`.
### Scientific Notation
You can use scientific notation by using the `-e` option (exponent after that). For example, you can do `1.23 -e 100`, which means 1.23 * 10^100.

### Reading and writing from files
You can read the number from a file by using the `-f`, `--file` option. This option is useful if you're trying to spell a number so large that the terminal can't handle it. Only the number should be in the file, in one line, no commas.

If you are reading from a file because a number is that large, you probably want to also output the number to a file, since the terminal might not be able to display that much text. You can do that by either redirecting the output with `>`, or using the `-o`, `--output` option. If the file already exists, it will be overwritten.

## About this project
I'm a beginner, still learning programming, and I made this project mostly for practice. So the code might not be perfect 😅 Still I tried to make it as good and efficient as I could.

Feel free to contribute or give feedback - I'd appreciate it!
