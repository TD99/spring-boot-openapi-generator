package dev.timduerr.openapigeneratorexample;

/**
 * TestModels.
 *
 * @author Tim Dürr
 * @version 1.0
 */
public class TestModels {

    /**
     * A simple entity.
     */
    @SuppressWarnings("unused")
    public static class TestEntityWithHiddenProperty {
        private String fallback;
        private String title;
        private String secret;

        public String getFallback() {
            return fallback;
        }

        public String getTitle() {
            return title;
        }

        public String getSecret() {
            return secret;
        }
    }

    /**
     * A simple DTO with a hidden property.
     */
    @SuppressWarnings("unused")
    public static class TestDtoNotExposingHiddenProperty {
        private String fallback;
        private String title;

        public String getFallback() {
            return fallback;
        }

        public String getTitle() {
            return title;
        }
    }
}
