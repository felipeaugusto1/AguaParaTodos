package felipe.com.br.aguaparatodos.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by felipe on 9/15/15.
 */
public class SearchableProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "felipe.com.br.aguaparatodos.provider.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
