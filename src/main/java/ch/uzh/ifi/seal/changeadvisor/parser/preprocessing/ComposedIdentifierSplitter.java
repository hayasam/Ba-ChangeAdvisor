package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Splits composed identifies (e.g. CamelCase, snake_case, and digit separated text) into tokens.
 * Created by alex on 14.07.2017.
 */
class ComposedIdentifierSplitter {

    private static final Pattern DIGIT_SEPARATED_TEXT_PATTERN = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

    static List<String> split(String text) {
        if (text.length() < 2) {
            return ImmutableList.of(text);
        }
        text = splitCamelCase(text);
        text = splitUnderScoreText(text);
        text = splitDigitSeparatedText(text);
        return Splitter.on(" ").omitEmptyStrings().trimResults().splitToList(text);
    }

    private static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    private static String splitUnderScoreText(String s) {
        Iterable<String> split = Splitter.on('_').omitEmptyStrings().trimResults().split(s);
        return String.join(" ", split);
    }

    private static String splitDigitSeparatedText(String s) {
        Iterable<String> split = Splitter.on(DIGIT_SEPARATED_TEXT_PATTERN).trimResults().omitEmptyStrings().split(s);
        return String.join(" ", split);
    }
}
