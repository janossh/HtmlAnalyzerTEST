import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.Optional;

public class HtmlAnalyzer {
    private SiblingsChecker siblingsChecker;

    public HtmlAnalyzer(SiblingsChecker siblingsChecker) {
        this.siblingsChecker = siblingsChecker;
    }

    public Optional<Element> findSiblingElement(Element original, Document document) {
        return document
                .getAllElements()
                .stream()
                .filter(element -> !element.equals(document))
                .map(element -> new SimpleEntry<>(element, siblingsChecker.calculateSiblingIndex(original, element)))
                .max(Comparator.comparingLong(SimpleEntry::getValue))
                .map(SimpleEntry::getKey);
    }

    public String getSiblingDetails(Element original, Element similar) {
        return siblingsChecker.buildSiblingsDetails(original, similar);
    }
}
