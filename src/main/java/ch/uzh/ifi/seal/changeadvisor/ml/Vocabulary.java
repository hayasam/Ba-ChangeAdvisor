package ch.uzh.ifi.seal.changeadvisor.ml;

import java.util.*;

/**
 * Created by alex on 24.07.2017.
 */
public class Vocabulary {

    /**
     * Flat token list.
     */
    private List<String> vocabs;

    /**
     * Token -> "id" map. id is the insertion order of each token.
     */
    private Map<String, Integer> tokenIds;

    /**
     * Document ids.
     */
    private List<List<Integer>> documentIds;

    /**
     * @param documents list of document tokens (Tokens by document).
     */
    public Vocabulary(List<Set<String>> documents) {
        int initSize = getInitialSize(documents);
        vocabs = new ArrayList<>(initSize);
        tokenIds = new HashMap<>(initSize);
        this.documentIds = new ArrayList<>(initSize);

        for (Set<String> document : documents) {
            List<Integer> ids = documentToIds(document);
            this.documentIds.add(ids);
        }
    }

    private int getInitialSize(List<Set<String>> documents) {
        int docs = documents.size();
        int size = 0;
        if (!documents.isEmpty()) {
            size = documents.get(0).size();
        }
        return docs * size;
    }

    private List<Integer> documentToIds(Set<String> document) {
        List<Integer> ids = new ArrayList<>();
        for (String token : document) {
            ids.add(vocabToId(token));
        }
        return ids;

    }

    private int vocabToId(String token) {
        if (!tokenIds.containsKey(token)) {
            int id = vocabs.size();
            tokenIds.put(token, id);
            vocabs.add(token);
            return id;
        }
        return tokenIds.get(token);
    }

    public List<String> getVocabs() {
        return vocabs;
    }

    public Map<String, Integer> getTokenIds() {
        return tokenIds;
    }

    public List<List<Integer>> getDocumentIds() {
        return documentIds;
    }
}
