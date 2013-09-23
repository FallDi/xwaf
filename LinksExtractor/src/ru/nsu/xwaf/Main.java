package ru.nsu.xwaf;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.MalformedURLException;

public class Main {

    private static final int SITE_ITEM = 0;
    private static final int FILE_ITEM = 1;
    private static final int FULL_ARGUMENTS = 2;
    private static final int LINKS_LIMIT_AND_FULL_ARGUMENTS = 3;
    private static final String OUTPUT_FILE_NAME = "result.xml";

    public static void main(String[] args) {
        System.out.println(args[0]);
        LinksExtractor worker = new LinksExtractor();

        try {
            String outputFileName;
            int limit = 20;
            if (FILE_ITEM == args.length) {
                outputFileName = OUTPUT_FILE_NAME;
            } else if (FULL_ARGUMENTS == args.length) {
                outputFileName = args[FILE_ITEM];
            } else if (LINKS_LIMIT_AND_FULL_ARGUMENTS == args.length) {
                outputFileName = args[FILE_ITEM];
                limit = Integer.valueOf(args[FULL_ARGUMENTS]);
            } else {
                System.out.println("Usage: java -jar LinksExtractor.jar $LINK $RESULT_FILE_NAME $LINKS_LIMIT");
                return;
            }
            worker.start(args[SITE_ITEM], limit);
            worker.getResult(outputFileName);
        } catch (MalformedURLException error) {
            System.err.println("Sorry, we can\'t connect to the URL");
        } catch (IOException error) {
            error.printStackTrace(System.err);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (XMLStreamException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
