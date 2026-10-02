/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.apache.tika.Tika;


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

    String toHtml(Archive archive) throws IOException {
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
            sb.append(toHtml(archive, entry));
            sb.append("\n");
        }
        sb.append("</table>");
        return sb.toString();
    }

    String toHtml(Archive archive, Entry entry) throws IOException {
        Tika tika = new Tika();
        String sb = "<tr>" +
                "<td>" +
                entry.getName() +
                "</td>" +
                "<td>" +
                Instant.ofEpochMilli(entry.getTime()) +
                "</td>" +
                "<td>" +
                entry.getSize() +
                "</td>" +
                "<td>" +
                tika.detect(archive.getInputStream(entry)) +
                "</td>" +
                "</tr>";
        return sb;
    }
}
