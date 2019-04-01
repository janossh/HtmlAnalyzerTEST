import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PathBuilder {
    protected static final String PATH_SEPARATOR = " > ";

    public static String buildElementPath(Element element) {
        StringBuilder elementPath = new StringBuilder();
        Elements parents = element.parents();
        Collections.reverse(parents);
        parents.forEach(p -> elementPath
                .append(buildElementPosition(p))
                .append(PATH_SEPARATOR));
        return elementPath
                .append(buildElementPosition(element))
                .toString();
    }

    private static String buildElementPosition(Element element) {
        String result = element.tagName();
        if (element.hasParent()) {
            List<Element> parentsChildrenSameByTag = element
                    .parent()
                    .children()
                    .stream()
                    .filter(e -> e.tagName().equals(element.tagName()))
                    .collect(Collectors.toList());

            if (parentsChildrenSameByTag.size() > 1) {
                result += "[" + parentsChildrenSameByTag.indexOf(element) + "]";
            }
        }
        return result;
    }
}
