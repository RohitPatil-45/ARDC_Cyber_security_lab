package in.canaris.cloud.utils;

import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GuacIdentifierUtil {

    /** Encode id + prefix + datasource into Guacamole identifier (padding removed) */
    public static String encode(String id, String datasource) {
        return encode(id, "c", datasource); // default prefix "c" used by Guacamole for connections
    }

    public static String encode(String id, String prefix, String datasource) {
        byte[] idBytes = id.getBytes(StandardCharsets.UTF_8);
        byte[] prefixBytes = prefix == null ? new byte[0] : prefix.getBytes(StandardCharsets.UTF_8);
        byte[] dsBytes = datasource.getBytes(StandardCharsets.UTF_8);

        // layout: id + 0x00 + prefix + 0x00 + datasource
        int total = idBytes.length + 1 + (prefixBytes.length > 0 ? prefixBytes.length + 1 : 0) + dsBytes.length;
        byte[] raw = new byte[total];
        int pos = 0;

        System.arraycopy(idBytes, 0, raw, pos, idBytes.length); pos += idBytes.length;
        raw[pos++] = 0;

        if (prefixBytes.length > 0) {
            System.arraycopy(prefixBytes, 0, raw, pos, prefixBytes.length); pos += prefixBytes.length;
            raw[pos++] = 0;
        }

        System.arraycopy(dsBytes, 0, raw, pos, dsBytes.length);

        // Guacamole identifier in the UI strips '=' padding
        return Base64.getEncoder().withoutPadding().encodeToString(raw);
    }

    /** Decode a Guacamole identifier back to its parts */
    public static Decoded decode(String identifier) {
        // add padding so Java's decoder can accept it
        String padded = identifier;
        int mod = identifier.length() % 4;
        if (mod != 0) {
            int need = 4 - mod;
            StringBuilder sb = new StringBuilder(identifier);
            for (int i = 0; i < need; i++) sb.append('=');
            padded = sb.toString();
        }

        byte[] raw = Base64.getDecoder().decode(padded);

        int i = 0;
        while (i < raw.length && raw[i] != 0) i++;
        String id = new String(Arrays.copyOfRange(raw, 0, i), StandardCharsets.UTF_8);
        i++; // skip first null

        // find next null (if any)
        int j = i;
        while (j < raw.length && raw[j] != 0) j++;

        String prefix = null;
        String datasource;
        if (j < raw.length) {
            prefix = new String(Arrays.copyOfRange(raw, i, j), StandardCharsets.UTF_8);
            datasource = new String(Arrays.copyOfRange(raw, j + 1, raw.length), StandardCharsets.UTF_8);
        } else {
            // no second null -> no prefix, remainder is datasource
            prefix = null;
            datasource = new String(Arrays.copyOfRange(raw, i, raw.length), StandardCharsets.UTF_8);
        }

        return new Decoded(id, prefix, datasource);
    }

    public static class Decoded {
        public final String id;
        public final String prefix;
        public final String datasource;
        public Decoded(String id, String prefix, String datasource) {
            this.id = id; this.prefix = prefix; this.datasource = datasource;
        }
        @Override public String toString() {
            return "id=" + id + ", prefix=" + prefix + ", datasource=" + datasource;
        }
    }

    
}
