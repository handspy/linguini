package pt.up.hs.linguini;

import pt.up.hs.linguini.exceptions.ConfigException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration of the library.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Config {
    private static final String FILE_PATH_FORMAT = "/%s/config.properties";

    private static final String DICTIONARY_EXCLUSIONS_PROP = "dictionaryExclusions";
    private static final String CUSTOM_DICTIONARIES_PROP = "customDictionaries";
    private static final String LEXICAL_CONVERSIONS_PROP = "lexicalConversions";
    private static final String GRAMMATICAL_CONVERSIONS_PROP = "grammaticalConversions";
    private static final String FUNCTIONAL_WORD_TAGS_PROP = "functionalWordTags";
    private static final String CONTENT_WORD_TAGS_PROP = "contentWordTags";

    private static Map<Locale, Config> configs = new HashMap<>();

    private Locale locale;

    private Properties properties;

    private Config(Locale locale, Properties properties) {
        this.locale = locale;
        this.properties = properties;
    }

    public static Config getInstance() throws ConfigException {
        return getInstance(Locale.ENGLISH);
    }

    public static Config getInstance(Locale locale) throws ConfigException {
        if (!configs.containsKey(locale)) {
            Properties properties = new Properties();
            String path = String.format(FILE_PATH_FORMAT, locale.toString());
            try (
                    InputStream is = Config.class.getResourceAsStream(path)
            ) {
                properties.load(is);
            } catch (IOException e) {
                throw new ConfigException(e);
            }
            configs.put(locale, new Config(locale, properties));
        }
        return configs.get(locale);
    }

    public String[] getCustomDictionaries() {
        String value = get(CUSTOM_DICTIONARIES_PROP);
        if (value == null) {
            return new String[0];
        }
        return value.split(";");
    }

    public String getDictionaryExclusions() {
        return get(DICTIONARY_EXCLUSIONS_PROP);
    }

    public String getContentWordTags() {
        return get(CONTENT_WORD_TAGS_PROP);
    }

    public String getFunctionalWordTags() {
        return get(FUNCTIONAL_WORD_TAGS_PROP);
    }

    public Map<String, String> getLexicalConversions() {
        Map<String, String> lexiconConversions = new HashMap<>();
        String value = get(LEXICAL_CONVERSIONS_PROP);
        if (value == null) {
            return lexiconConversions;
        }
        String[] conversions = value.split(";");
        for (String conversion : conversions) {
            if (conversion.contains(":") && (conversion.indexOf(":") > 0)
                    && (conversion.indexOf(":") < conversion.length() - 1)) {
                lexiconConversions.put(conversion.substring(0, conversion.indexOf(":")),
                        conversion.substring(conversion.indexOf(":") + 1));
            }
        }
        return lexiconConversions;
    }

    public Map<String, String> getGrammaticalConversions() {
        Map<String, String> grammaticalConversions = new HashMap<>();
        String value = get(GRAMMATICAL_CONVERSIONS_PROP);
        if (value == null) {
            return grammaticalConversions;
        }
        String[] conversions = value.split(";");
        for (String conversion : conversions) {
            if (conversion.contains(":") && (conversion.indexOf(":") > 0)
                    && (conversion.indexOf(":") < conversion.length() - 1)) {
                grammaticalConversions
                        .put(
                                conversion.substring(0, conversion.indexOf(":")),
                                conversion.substring(conversion.indexOf(":") + 1)
                        );
            }
        }
        return grammaticalConversions;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
