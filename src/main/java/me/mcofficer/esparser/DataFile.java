/*
DataFile.java
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DataFile {

    private DataNode root;

    public DataFile(String path) throws IOException {
        root = new DataNode(null, null, null);
        load(path);
    }

    public ArrayList<DataNode> getNodes() {
        return root.getChildren();
    }

    public ArrayList<DataNode> getNodesReversed() {
        return root.getChildrenReversed();
    }

    private void load(String path) throws IOException {
        List<String> data = Files.readAllLines(Paths.get(path));
        parse(data);
    }

    private void parse(List<String> data) {
        Stack<DataNode> stack = new Stack<>();
        stack.add(root);
        Stack<Integer> whiteStack = new Stack<>();

        for (String line: data) {
            char[] chars = line.toCharArray();
            int i = 0;
            int white = 0;
            while (Character.isWhitespace(chars[i]) && chars[i] != '\n') {
                white += 1;
                i += 1;
            }

            if (chars[i] == '#' | chars[i] == '\n')
                continue;

            while (whiteStack.get(whiteStack.size() - 1) >= white) {
                whiteStack.pop();
                stack.pop();
            }

            DataNode node = new DataNode(null, null, null);
            stack.get(stack.size() - 1).append(node);

            stack.add(node);
            whiteStack.add(white);

            while (chars[i] != '\n') {
                char endQuote = chars[i];
                boolean isQuoted = false;
                if (endQuote == '"' || endQuote == '`') {
                    isQuoted = true;
                    i += 1;
                }

                String token = "";
                if (isQuoted) {
                    while (chars[i] != '\n' || chars[i] != endQuote) {
                        token += chars[i];
                        i += 1;
                    }
                    if (chars[i] != endQuote)
                        node.printTrace("Closing Quote is missing");
                    i += 1;
                }
                else {
                    while (!Character.isWhitespace(chars[i])) {
                        token += chars[i];
                        i += 1;
                    }
                }
                node.getTokens().add(token);

                if (chars[i] != '\n')
                        while (Character.isWhitespace(chars[i]) && chars[i] != '\n')
                            i += 1;
                if (chars[i] == '#')
                    break;
            }
        }
    }

    public void append(DataNode node) {
        root.append(node);
    }

    public void remove(DataNode node) {
        root.remove(node);
    }
}
