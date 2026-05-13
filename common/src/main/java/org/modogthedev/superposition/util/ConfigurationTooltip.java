package org.modogthedev.superposition.util;

public interface ConfigurationTooltip {
    void execute();

    abstract class Editable {
        private final String name;

        public Editable(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract String getEditingText();
        public abstract void setEditingText(String text);

        public String getTag() {
            return name;
        }


        public String getStringKey() {
            return name;
        }

        public EditableTooltip getEditable(int line) {
            return new EditableTooltip() {
                @Override
                public String getText() {
                    return getEditingText();
                }

                @Override
                public void replaceText(String string) {
                    setEditingText(string);
                }

                @Override
                public int lineOffset() {
                    return line;
                }

                @Override
                public String prefix() {
                    return name + " -";
                }

                @Override
                public String tagName() {
                    return getTag();
                }

                @Override
                public String stringKey() {
                    return getStringKey();
                }
            };
        }
    }
}
