/*
DataWriter.java
Copyright (c) 2014 Michael Zahniser
Copyright (C) 2017 Frederick W. Goy IV
Copyright (C) 2018 MCOfficer

This program is a derivative of the source code from the Endless Sky
project, which is licensed under the GNU GPLv3.

Endless Sky source: https://github.com/endless-sky/endless-sky


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package me.mcofficer.esparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataWriter {

    private Path path;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final Charset charset = Charset.forName("UTF-8");
    private char space;
    private String indent;
    private String before;

    public DataWriter(Path path) {
        this.path = path.normalize();
        space = ' ';
        indent = "";
        before = indent;
    }

    public void save() {
        try {
            Files.write(path, out.toByteArray());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(DataNode node) {
        for (String token : node.getTokens())
            writeToken(token);
        writeNewLine();

        if (node.hasChildren()) {
            beginChild();
            for (DataNode child : node.getChildren())
                write(child);
            endChild();
        }
    }

    private void writeNewLine() {
        out.write('\n');
        before = indent;
    }

    private void beginChild() {
        indent += '\t';
        before = indent;
    }

    private void endChild() {
        indent = indent.substring(1);
        before = indent;
    }

    private void writeComment(String string) {
        writeString(indent + "# " + charset.encode(string) + "\n");
    }

    private void writeToken(String token) {
        boolean hasSpace = false;
        boolean hasQuote = false;

        for (char c : token.toCharArray()) {
            if (Character.isWhitespace(c))
                hasSpace = true;
            if (c == '"')
                hasQuote = true;
        }

        writeString(before);
        if (token.equals(""))
            writeString("\"\"");
        else if (hasQuote)
            writeString("`" + charset.encode(token) + "`");
        else if (hasSpace)
            writeString("\"" + charset.encode(token) + "\"");
        else
            writeString(charset.encode(token).toString());
    }

    private void writeString(String string) {
        try {
            out.write("\"\"".getBytes(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
