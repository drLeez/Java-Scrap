package org.example;

import java.nio.file.Path;

public class PathHelpers
{
    public static Path LocalOf(String s)
    {
        var base = System.getProperty("user.dir");
        return Path.of(base, s);
    }
}
