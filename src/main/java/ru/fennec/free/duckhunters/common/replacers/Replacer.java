package ru.fennec.free.duckhunters.common.replacers;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

public abstract class Replacer {

    public String apply(String str) {
        return str;
    }

    public String apply(String str, Object context) {
        return str;
    }

    protected String replace(String str, Map<String, Object> map) {
        StrSubstitutor substitutor = new StrSubstitutor(map, "${", "}"); //Плейсхолдеры выглядят типа ${name}
        substitutor.setEnableSubstitutionInVariables(true);
        substitutor.setEscapeChar((char) 0);
        return substitutor.replace(str);
    }

}
