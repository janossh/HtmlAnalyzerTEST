import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;

public class App {

    private static final String CHARSET_NAME = "utf8";

    public static void main(String[] args) throws RuntimeException {

        InputProperties properties = InputProperties
                .parseInputProperties(args)
                .orElseThrow(() -> new RuntimeException("Can not parse properties from args."));

        Document originalDocument = parseDocument(properties.getOriginalFilePath())
                .orElseThrow(() -> new RuntimeException("Can not parse original document."));

        Element originalElement = originalDocument.getElementById(properties.getElementId());
        if (Objects.isNull(originalElement)) {
            throw new RuntimeException(format("Can not find element with id: [%s] inside original document.", properties.getElementId()));
        }

        Document diffDocument = parseDocument(properties.getDiffFilePath())
                .orElseThrow(() -> new RuntimeException("Can not parse diff document."));

        HtmlAnalyzer htmlAnalyzer = buildWeightedHtmlAnalyzer();

        Element siblingElement = htmlAnalyzer
                .findSiblingElement(originalElement, diffDocument)
                .orElseThrow(() -> new RuntimeException(
                        format("Can not find element similar to [%s] into diff document.", originalElement.toString())));

        System.out.println(format("Path to similar element: [%s]", PathBuilder.buildElementPath(siblingElement)));
        System.out.println(format("\nSimilar element: [%s]", siblingElement));
        System.out.println(format("\nContribution details: [\n%s]", htmlAnalyzer.getSiblingDetails(originalElement, siblingElement)));
    }

    private static HtmlAnalyzer buildWeightedHtmlAnalyzer() {
        Map<String, Integer> attributeWeights = new HashMap<>();
        attributeWeights.put("ID", 2);
        attributeWeights.put("CLASS", 2);

        Map<Function<Element, String>, Integer> functionWeights = new HashMap<>();
        functionWeights.put(Element::tagName, 3);
        functionWeights.put(Element::text, 3);

        return new HtmlAnalyzer(new SiblingsChecker(attributeWeights, functionWeights));
    }

    private static Optional<Document> parseDocument(String filePath) {
        try {
            Document doc = Jsoup.parse(new File(filePath), CHARSET_NAME);
            return Optional.of(doc);
        } catch (IOException e) {
            return Optional.empty();
        }
    }


}
