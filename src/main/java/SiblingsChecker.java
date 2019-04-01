import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import java.util.Map;
import java.util.function.Function;

public class SiblingsChecker {
    private static final Integer DEFAULT_ATTRIBUTE_WEIGHT = 1;

    private Map<String, Integer> attributeWeights;
    private Map<Function<Element, String>, Integer> functionWeights;

    public SiblingsChecker(Map<String, Integer> attributeWeights, Map<Function<Element, String>, Integer> functionWeights) {
        this.attributeWeights = attributeWeights;
        this.functionWeights = functionWeights;
    }

    public long calculateSiblingIndex(Element original, Element tested) {
        int siblingsIndexByAttributes = original
                .attributes()
                .asList()
                .stream()
                .mapToInt(attribute -> calculateAttributeContribution(tested.attributes(), attribute))
                .sum();

        int siblingsIndexByFunctions = functionWeights
                .entrySet()
                .stream()
                .mapToInt(entry -> calculateFunctionContribution(original, tested, entry))
                .sum();

        return siblingsIndexByAttributes + siblingsIndexByFunctions;
    }

    private int calculateFunctionContribution(Element original,
                                              Element tested,
                                              Map.Entry<Function<Element, String>, Integer> entry) {
        Function<Element, String> function = entry.getKey();
        boolean functionsHaveEqualValues = StringUtils
                .equalsIgnoreCase(function.apply(original), function.apply(tested));
        return (functionsHaveEqualValues ? 1 : 0) * entry.getValue();
    }

    private int calculateAttributeContribution(Attributes testedElementAttributes, Attribute attribute) {
        String attributeKey = attribute.getKey();
        boolean isTestedElementHasEqualAttribute = attribute
                .getValue()
                .equalsIgnoreCase(testedElementAttributes.getIgnoreCase(attributeKey));
        Integer attributeWeight = attributeWeights
                .getOrDefault(attributeKey.toUpperCase(), DEFAULT_ATTRIBUTE_WEIGHT);
        return (isTestedElementHasEqualAttribute ? 1 : 0) * attributeWeight;
    }

    public String buildSiblingsDetails(Element original, Element tested) {
        StringBuilder sb = new StringBuilder();
        original
                .attributes()
                .asList()
                .forEach(attribute -> sb.append(buildAttributeContributionDetails(original, tested, attribute)));
        functionWeights
                .entrySet()
                .forEach(entry -> sb.append(buildFunctionContributionDetails(original, tested, entry)));

        sb.append("Total similarity index: ").append(calculateSiblingIndex(original, tested));
        return sb.toString();
    }

    private String buildAttributeContributionDetails(Element original, Element tested, Attribute attribute) {
        StringBuilder sb = new StringBuilder();
        String attributeName = attribute.getKey();
        Attributes testedAttributes = tested.attributes();
        sb.append("Attribute: [").append(attributeName).append("] ")
                .append("\n\toriginal element value: [").append(original.attributes().getIgnoreCase(attributeName)).append("], ")
                .append("\n\tsimilar element value: [").append(testedAttributes.getIgnoreCase(attributeName)).append("], ")
                .append("\n\tcontribution: ").append(calculateAttributeContribution(testedAttributes, attribute))
                .append("\n");
        return sb.toString();
    }

    private String buildFunctionContributionDetails(Element original,
                                                    Element tested,
                                                    Map.Entry<Function<Element, String>, Integer> entry) {
        StringBuilder sb = new StringBuilder();
        String functionName = entry.getKey().toString();
        sb.append("Function: [").append(functionName).append("] ")
                .append("\n\toriginal element value: [").append(entry.getKey().apply(original)).append("], ")
                .append("\n\tsimilar element value: [").append(entry.getKey().apply(tested)).append("], ")
                .append("\n\tcontribution: ").append(calculateFunctionContribution(original, tested, entry))
                .append("\n");
        return sb.toString();
    }

    public void setAttributeWeights(Map<String, Integer> attributeWeights) {
        this.attributeWeights = attributeWeights;
    }

    public void setFunctionWeights(Map<Function<Element, String>, Integer> functionWeights) {
        this.functionWeights = functionWeights;
    }
}
