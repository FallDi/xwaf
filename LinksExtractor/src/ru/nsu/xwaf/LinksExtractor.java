package ru.nsu.xwaf;

import javax.xml.stream.*;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class LinksExtractor {

    public void start(String link, int size) throws MalformedURLException {
        assert null != link;

        URL linkURL;


        try {
            linkURL = new URL(link);
            this.host = linkURL.getHost();

            Document doc = Jsoup.connect(link).get();

            queue.addAll(doc.getElementsByTag(LINK_TAG));

            while (false == queue.isEmpty() && size > links.size()) {
                Element nextLink = queue.poll();
                if (false == nextLink.absUrl(LINK_TAG_MODIFIER).contains(ITSELF_REFERENCE_FLAG)) {
                    extractLinksFromPage(nextLink);
                }
            }
        } catch (MalformedURLException error) {
            throw error;
        } catch (IOException error) {
            error.printStackTrace(System.err);
        }
    }

    public List<String> getResult() {
        assert 0 != links.size();

        List<String> linksInString = new ArrayList<String>();
        for (Element element : links) {
            linksInString.add(element.absUrl(LINK_TAG_MODIFIER));
        }
        linksInString = deleteDublicates(linksInString);

        return linksInString;
    }

    public void getResult(String file) throws XMLStreamException, TransformerException, ParserConfigurationException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element rootElement = doc.createElement("root");
        doc.appendChild(rootElement);


        for (Element nextLink : links) {

            org.w3c.dom.Element item = doc.createElement("item");
            org.w3c.dom.Element url = doc.createElement("url");
            rootElement.appendChild(item);
            item.appendChild(url);
            url.appendChild(doc.createTextNode(nextLink.absUrl(LINK_TAG_MODIFIER)));

            Map<String, String> coockies = (Jsoup.connect(nextLink.absUrl(LINK_TAG_MODIFIER)).method(Connection.Method.GET).execute().cookies());

            org.w3c.dom.Element cookies = doc.createElement("cookies");
            item.appendChild(cookies);
            for (String nextKey : coockies.keySet()) {

                org.w3c.dom.Element cookie = doc.createElement("cookie");
                cookies.appendChild(cookie);


                org.w3c.dom.Element key = doc.createElement("key");
                cookie.appendChild(key);
                key.appendChild(doc.createTextNode(nextKey));

                org.w3c.dom.Element value = doc.createElement("value");
                cookie.appendChild(value);
                value.appendChild(doc.createTextNode(coockies.get(nextKey)));
            }


            Map<String, String> headers = Jsoup.connect(nextLink.absUrl(LINK_TAG_MODIFIER)).method(Connection.Method.GET).execute().cookies();

            org.w3c.dom.Element headersNode = doc.createElement("parametrs");
            item.appendChild(headersNode);
            for (String nextKey : headers.keySet()) {

                org.w3c.dom.Element header = doc.createElement("header");
                cookies.appendChild(header);


                org.w3c.dom.Element key = doc.createElement("key");
                header.appendChild(key);
                key.appendChild(doc.createTextNode(nextKey));

                org.w3c.dom.Element value = doc.createElement("value");
                header.appendChild(value);
                value.appendChild(doc.createTextNode(coockies.get(nextKey)));
            }


        }
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(file));

        transformer.transform(source, result);

    }

    private void extractLinksFromPage(Element nextLink) throws IOException {
        assert null != nextLink;

        Document doc;

        try {
            doc = Jsoup.connect(nextLink.absUrl(LINK_TAG_MODIFIER)).get();
        } catch (IOException error) {
            links.remove(nextLink);
            return;
        } catch (IllegalArgumentException error) {
            links.remove(nextLink);
            return;
        }


        Elements links = doc.getElementsByTag(LINK_TAG);
        for (Element link : links) {

            if (isNewLink(link) && (false == this.links.contains(link))) {

                continue;
            } else {
                this.queue.add(link);
            }
        }
        if (isNewLink(nextLink)) {
            this.links.add(nextLink);
        }

    }

    private static List<String> deleteDublicates(List<String> args) {
        List<String> massive = new ArrayList<String>();
        for (String currentArgs : args) {
            String currentString = null;
            if (currentArgs.contains(DELIMETER_AND)) {
                String[] firstSeparation = currentArgs.split("\\" + DELIMETER_AND);
                for (String firstSeparationCurrentString : firstSeparation) {
                    String[] secondSeparation = firstSeparationCurrentString.split("\\" + DELIMETER_QUESTION);
                    if (null == currentString) {
                        currentString = secondSeparation[0];
                    }
                    if (null != currentString) {
                        currentString += DELIMETER_AND;
                    }
                    for (int i = 1; i < secondSeparation.length; i++) {
                        String argument = "";
                        int position = 0;
                        while (65 <= secondSeparation[i].charAt(position) && 122 >= secondSeparation[i].charAt(position)) {
                            argument += secondSeparation[i].charAt(position);
                            position++;
                        }
                        String value = "";
                        for (int curPos = position; curPos < secondSeparation[i].length(); curPos++) {
                            if ('0' <= secondSeparation[i].charAt(curPos) && '9' >= secondSeparation[i].charAt(curPos)) {
                                value += 'x';
                                continue;
                            }
                            value += secondSeparation[i].charAt(curPos);
                        }
                        currentString += DELIMETER_QUESTION + argument + value;
                    }
                }
            } else if (currentArgs.contains(DELIMETER_QUESTION)) {
                String[] firstseparation = currentArgs.split("\\" + DELIMETER_QUESTION);
                currentString = firstseparation[0];
                for (int i = 1; i < firstseparation.length; i++) {
                    String argument = "";
                    int position = 0;
                    while (65 <= firstseparation[i].charAt(position) && 122 >= firstseparation[i].charAt(position)) {
                        argument += firstseparation[i].charAt(position);
                        position++;
                    }
                    String value = "";
                    for (int curPos = position; curPos < firstseparation[i].length(); curPos++) {
                        if ('0' <= firstseparation[i].charAt(curPos) && '9' >= firstseparation[i].charAt(curPos)) {
                            value += 'x';
                            continue;
                        }
                        value += firstseparation[i].charAt(curPos);
                    }
                    currentString += DELIMETER_QUESTION + argument + value;
                }
            } else {
                currentString = null;
            }
            if (!massive.contains(currentString) && null != currentString) {
                massive.add(currentString);
            }
        }
        return massive;
    }

    private static String getTemplate(String args) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(args);

        deleteDublicates(arrayList);
        return arrayList.get(0);
    }

    private boolean isNewLink(Element link) {
        boolean result = true;

        result &= link.absUrl(LINK_TAG_MODIFIER).contains(host);
        result &= false == links.contains(link);
        result &= false == link.absUrl(LINK_TAG_MODIFIER).contains(ITSELF_REFERENCE_FLAG);
        if (templates.contains(getTemplate(link.absUrl(LINK_TAG_MODIFIER)))) {
            result = false;
            templates.add(getTemplate(link.absUrl(LINK_TAG_MODIFIER)));
        }
        return result;
    }
    public static final String DELIMETER_AND = "&";
    public static final String DELIMETER_QUESTION = "?";
    private static final String LINK_TAG = "a";
    private static final String LINK_TAG_MODIFIER = "href";
    private static final String ITSELF_REFERENCE_FLAG = "#";
    private final List<String> templates = new ArrayList<String>();
    private final Elements links = new Elements();
    private String host;
    private final Queue<Element> queue = new LinkedList<Element>();
}
