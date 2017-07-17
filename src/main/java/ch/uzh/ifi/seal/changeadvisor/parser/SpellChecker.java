package ch.uzh.ifi.seal.changeadvisor.parser;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by alex on 17.07.2017.
 */
public class SpellChecker {

    private static final Language ENGLISH = new BritishEnglish();
    private static final char EMPTY_SPACE = ' ';
    private JLanguageTool languageTool;

    public SpellChecker() {
        languageTool = new JLanguageTool(ENGLISH);
    }

    public String correct(String text) throws IOException {
        List<RuleMatch> matches = languageTool.check(text);
        ListIterator<RuleMatch> iterator = matches.listIterator(matches.size());

        while (iterator.hasPrevious()) {
            final RuleMatch match = iterator.previous();

            List<String> suggestedReplacements = match.getSuggestedReplacements();
            int fromPos = match.getFromPos();
            int toPos = match.getToPos();
            String suggestion = suggestedReplacements.get(0);

            String textBefore = text.substring(0, fromPos);
            String textAfter = text.substring(toPos);
            text = textBefore + suggestion + textAfter;
        }

        return text;
    }
}
