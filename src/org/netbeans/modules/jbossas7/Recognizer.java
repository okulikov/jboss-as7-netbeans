/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.jbossas7;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kulikov
 */
public class Recognizer {

    private StringBuilder builder = new StringBuilder();

    public List<String> process(InputStream in) throws IOException {
        ArrayList<String> tokens = new ArrayList();

        int b;
        while ((b = in.read()) != -1) {
            char c = (char)b;
            switch (c) {
                case '\t' :
                case '\r' :
                case '\n' :
                    tokens.add(builder.toString());
System.out.println("Tokens: " + tokens);
                    builder = new StringBuilder();
                    break;
                default :
                    builder.append(c);
            }
        }
System.out.println("Done with reading");
        return tokens;
    }
}
