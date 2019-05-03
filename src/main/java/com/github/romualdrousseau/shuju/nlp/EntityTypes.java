package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;

public class EntityTypes {
    private ArrayList<String> types = new ArrayList<String>();
    private HashMap<String, String> patterns = new HashMap<String, String>();
    private int maxVectorSize = 0;

    public EntityTypes(int maxVectorSize) {
        this.maxVectorSize = maxVectorSize;
    }

    public EntityTypes(JSONObject json) {
        this.maxVectorSize = json.getInt("maxVectorSize");

        JSONArray jsonTypes = json.getJSONArray("types");
        for(int i = 0; i < jsonTypes.size(); i++) {
            String s = jsonTypes.getString(i);
            this.types.add(s);
        }

        JSONArray jsonPatterns = json.getJSONArray("patterns");
        for(int i = 0; i < jsonPatterns.size(); i++) {
            JSONObject entity = jsonPatterns.getJSONObject(i);
            String p = entity.getString("pattern");
            String t = entity.getString("type");
            this.patterns.put(p, t);
        }
    }

    public List<String> types() {
        return this.types;
    }

    public int ordinal(String type) {
        return this.types.indexOf(type);
    }

    public void registerType(String type) {
        assert this.types.indexOf(type) == -1;
        this.types.add(type);
    }

    public void registerPattern(String pattern, String type) {
        assert this.types.indexOf(type) > 0;
        this.patterns.put(pattern, type);
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.maxVectorSize);

        if(w == null) {
            return result;
        }

        for (String pattern : this.patterns.keySet()) {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(w);
            if (m.find()) {
                String t = this.patterns.get(pattern);
                result.set(this.ordinal(t), 1.0f);
            }
        }

        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonTypes = JSON.newJSONArray();
        for (String t : this.types) {
            jsonTypes.append(t);
        }

        JSONArray jsonPatterns = JSON.newJSONArray();
        for (String p : this.patterns.keySet()) {
            String t = this.patterns.get(p);
            JSONObject entity =  JSON.newJSONObject();
            entity.setString("pattern", p);
            entity.setString("type", t.toString());
            jsonPatterns.append(entity);
        }

        JSONObject json = JSON.newJSONObject();
        json.setInt("maxVectorSize", this.maxVectorSize);
        json.setJSONArray("types", jsonTypes);
        json.setJSONArray("patterns", jsonPatterns);
        return json;
    }
}
