package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;

public class StringList implements BaseList {
    private ArrayList<String> strings = new ArrayList<String>();
    private int vectorSize = 0;

    public StringList(int vectorSize) {
        this.vectorSize = vectorSize;
    }

    public StringList(int vectorSize, String[] strings) {
        this.vectorSize = vectorSize;
        this.strings.addAll(Arrays.asList(strings));
    }

    public StringList(JSONObject json) {
        this.vectorSize = json.getInt("maxVectorSize");
        JSONArray jsonTypes = json.getJSONArray("types");
        for(int i = 0; i < jsonTypes.size(); i++) {
            String s = jsonTypes.getString(i);
            this.strings.add(s);
        }
    }

    public List<String> values() {
        return this.strings;
    }

    public int size() {
        return this.strings.size();
    }

    public String get(int i) {
        return this.strings.get(i);
    }

    public int ordinal(String w) {
        return this.strings.indexOf(w);
    }

    public StringList add(String w) {
        assert this.strings.indexOf(w) == -1;
        this.strings.add(w);
        return this;
    }

    public int getVectorSize() {
        return this.vectorSize;
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.vectorSize);

        if(w == null) {
            return result;
        }

        result.oneHot(this.ordinal(w));

        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonTypes = JSON.newJSONArray();
        for (String t : this.strings) {
            jsonTypes.append(t);
        }

        JSONObject json = JSON.newJSONObject();
        json.setInt("maxVectorSize", this.vectorSize);
        json.setJSONArray("types", jsonTypes);
        return json;
    }
}