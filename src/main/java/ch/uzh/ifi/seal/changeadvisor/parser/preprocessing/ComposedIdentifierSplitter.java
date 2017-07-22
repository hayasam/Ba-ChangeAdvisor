package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import java.util.regex.Pattern;

/**
 * Splits composed identifies (e.g. CamelCase, snake_case, and digit separated text) into tokens.
 * Created by alex on 14.07.2017.
 */
public class ComposedIdentifierSplitter {

    private static final Pattern DIGIT_SEPARATED_TEXT_PATTERN = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

    public String split(String text) {
        text = splitCamelCase(text);
        text = splitUnderScoreText(text);
        text = splitDigitSeparatedText(text);
        return text;
    }

    private String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    private String splitUnderScoreText(String s) {
        return s.replace('_', ' ');
    }

    private String splitDigitSeparatedText(String s) {
        return s.replaceAll(DIGIT_SEPARATED_TEXT_PATTERN.toString(), " ");
    }
}
