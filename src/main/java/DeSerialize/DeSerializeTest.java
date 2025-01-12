package DeSerialize;

import org.example.PathHelpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Stack;

public class DeSerializeTest
{
    public static void Init() throws IOException
    {
        var test = Files.readString(PathHelpers.LocalOf("assets/input.json"));

//        System.out.println(test);
        var obj = new JsonObject(null, null, JsonObject.JsonType.Array);
        try
        {
            obj.read(test);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        System.out.println();
    }

    public static class JsonObject
    {
        public LinkedList<JsonObject> children = new LinkedList<>();
        public String name, value;
        public JsonType type;

        public enum JsonType
        {
            Array,
            Object,
            String,
            Number,
            None
        }

        public JsonObject(String name, String value, JsonType type)
        {
            this.name = name;
            this.value = value;
            this.type = type;
        }

        private static String _complexLabel;
        public void read(String s)
        {
            s = s.trim();
            char first;
            while(!s.isEmpty())
            {
                first = s.charAt(0);
                if (first == '[' || first == '{')
                {
                    type = first == '[' ? JsonType.Array : JsonType.Object;
                    var end = FindLevelNonQuoteIndexOf(s, first, first == '[' ? ']' : '}');
                    var child = new JsonObject(_complexLabel, null, JsonType.None);
                    _complexLabel = null;
                    children.add(child);
                    child.read(s.substring(1, end));
                    s = s.substring(end + 1);
                }
                else if (first == '"')
                {
                    var colIndex = FindFirstNonQuoteIndexOf(s, ':');

                    var left = s.substring(0, colIndex).trim();
                    if (!left.startsWith("\"") || !left.endsWith("\"")) throw new IllegalArgumentException("JSON incorrectly formatted");
                    left = left.substring(1, left.length() - 1);

                    var right = s.substring(colIndex + 1).trim();
                    var rf = right.charAt(0);
                    if (rf == '"')
                    {
                        var end = right.indexOf('"', 1);
                        children.add(new JsonObject(left, right.substring(1, end), JsonType.String));
                        s = right.substring(end + 1);
                    }
                    else if (Character.isDigit(rf))
                    {
                        var end = right.indexOf(',');
                        if (end == -1) children.add(new JsonObject(left, right, JsonType.Number));
                        else
                        {
                            children.add(new JsonObject(left, right.substring(0, end), JsonType.Number));
                            s = right.substring(end);
                        }
                    }
                    else
                    {
                        if (rf == '[' || rf == '{')
                        {
                            s = right;
                            _complexLabel = left;
                            continue;
                        }

                        var end = right.indexOf(',');
                        var easySub = right.substring(0, end == -1 ? right.length() : end).trim();
                        if (Character.isDigit(rf))
                        {
                            children.add(new JsonObject(left, easySub, JsonType.Number));
                        }
                        else if (easySub.equals("null"))
                        {
                            children.add(new JsonObject(left, null, JsonType.Object));
                        }
                        s = end == -1 ? "" : right.substring(end);
                    }
                }
                else if (first == ',')
                {
                    s = s.substring(1).trim();
                }
                else
                {
                    throw new IllegalArgumentException("JSON contains illegal item start character");
                }
            }
        }

        private static int FindLevelNonQuoteIndexOf(String s, char open, char close)
        {
            int i = 0, level = 0;
            boolean inQuotes = false;
            char last = ' ';
            for (var c : s.toCharArray())
            {
                if (c == '"' && last != '\\') inQuotes = !inQuotes;
                else if (!inQuotes)
                {
                    if (c == open) level++;
                    else if (c == close)
                    {
                        if (--level == 0) return i;
                    }
                }
                last = c;
                i++;
            }
            return -1;
        }
        private static int FindFirstNonQuoteIndexOf(String s, char f)
        {
            int i = 0;
            boolean inQuotes = false;
            char last = ' ';
            for (var c : s.toCharArray())
            {
                if (c == '"' && last != '\\') inQuotes = !inQuotes;
                else if (c == f && !inQuotes) return i;
                last = c;
                i++;
            }
            return -1;
        }
    }
}
