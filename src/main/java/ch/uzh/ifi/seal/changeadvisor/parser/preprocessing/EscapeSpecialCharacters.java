package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

/**
 * Escapes any special charactes. Anything that is not a word or number.
 * Created by alex on 14.07.2017.
 */
class EscapeSpecialCharacters extends ProcessingStep implements CorpusEscaper {

    public String escape(String text) {
        text = text.replaceAll("[^\\w]", " ");
        text = text.replaceAll("\\d+", " ");
        return text;
    }

    @Override
    public String handle(String text) {
        text = escape(text);
        return next(text);
    }
}
