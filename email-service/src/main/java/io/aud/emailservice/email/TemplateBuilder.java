package io.aud.emailservice.email;

public class TemplateBuilder {

    private String template;

    public TemplateBuilder(String template) {
        this.template = template;
    }

    public TemplateBuilder replace(String key, String value){
        template = template.replace("{{" + key + "}}", value);
        return this;
    }

    @Override
    public String toString() {
        return template;
    }
}
