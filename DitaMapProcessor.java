import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class DitaMapProcessor {

    public static void main(String[] args) {
        String[] inputFilePaths = {
            "Maps\\Region\\ass\\countries\\angola\\content-types\\directory.ditamap"
        };

        try {
            for (String inputFilePath : inputFilePaths) {
                processFile(inputFilePath);
            }
            System.out.println("Content copied from all inputFilePaths to respective chapter files.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processFile(String inputFilePath) throws Exception {
        // Parse the input XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(inputFilePath));

        // Get the root element
        NodeList topicrefs = document.getElementsByTagName("topicref");
        NodeList chapters = document.getElementsByTagName("chapter");

        // Process each chapter element to create output filenames
        for (int i = 0; i < chapters.getLength(); i++) {
            Element chapter = (Element) chapters.item(i);
            String chapterId = chapter.getAttribute("id");

            if (chapterId != null && !chapterId.isEmpty()) {
                String chapterFilename = chapterId + ".dita";
                System.out.println("Chapter filename: " + chapterFilename);

                
                BufferedWriter initialWriter = new BufferedWriter(new FileWriter(chapterFilename));
                initialWriter.write("<?xml version=\"1.0\"?>\n");
                initialWriter.close();

                // Process each topicref element
                for (int j = 0; j < topicrefs.getLength(); j++) {
                    Element topicref = (Element) topicrefs.item(j);
                    String href = topicref.getAttribute("href");
                    System.out.println("href : " + href);

                    // Extract the filename from href
                    String filename = href.substring(href.lastIndexOf('/') + 1);
                    System.out.println("filename : " + filename);

                    // Read content from the referenced file
                    String contentFilePath = "" + href.replace('/', File.separatorChar);
                    System.out.println("contentFilePath : " + contentFilePath);
                    StringBuilder content = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new FileReader(contentFilePath));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Filter out XML declarations
                        if (!line.trim().equals("<?xml version=\"1.0\"?>")) {
                            content.append(line).append("\n");
                        }
                    }
                    reader.close();

                    // Append the content to the chapter file
                    BufferedWriter writer = new BufferedWriter(new FileWriter(chapterFilename, true));
                    writer.write(content.toString());
                    writer.close();
                }

                // Print the id of the chapter
                System.out.println("ID for chapter: " + chapterId);
            }
        }
    }
}
