/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;


/**
 * ArchivesMain.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-11-29 nsano initial version <br>
 */
public class ArchivesMain {

    /**
     * @param args 0: archive file
     */
    public static void main(String[] args) throws Exception {
        Path path = Paths.get(args[0]);
        Archive archive = Archives.getArchive(path.toFile());
        ArchivesMain app = new ArchivesMain();
        System.out.println(app.toHtml(archive));
    }

    String toHtml(Archive archive) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("\n");
        sb.append("<tr>");
        sb.append("<th>");
        sb.append("name");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("lastModified");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("size");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("type");
        sb.append("</th>");
        sb.append("</tr>");
        sb.append("\n");
        for (Entry entry : archive.entries()) {
            sb.append(toHtml(entry));
            sb.append("\n");
        }
        sb.append("</table>");
        return sb.toString();
    }

    String toHtml(Entry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<td>");
        sb.append(entry.getName());
        sb.append("</td>");
        sb.append("<td>");
        sb.append(Instant.ofEpochMilli(entry.getTime()));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(entry.getSize());
        sb.append("</td>");
        sb.append("<td>");
        sb.append(entry.getName());
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }
}
