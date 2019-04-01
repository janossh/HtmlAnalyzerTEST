import java.util.Optional;

public class InputProperties {
    private static final String MAKE_EVERYTHING_OK_BUTTON = "make-everything-ok-button";

    private static String originalFilePath;
    private static String diffFilePath;
    private static String elementId = MAKE_EVERYTHING_OK_BUTTON;

    public InputProperties(String originalFilePath, String diffFilePath, String elementId) {
        this.originalFilePath = originalFilePath;
        this.diffFilePath = diffFilePath;
        this.elementId = elementId;
    }

    public static Optional<InputProperties> parseInputProperties(String[] args) {
        if (args.length != 2) {
            return Optional.empty();
        }
        return Optional.of(new InputProperties(args[0], args[1], elementId));
    }

    public static String getOriginalFilePath() {
        return originalFilePath;
    }

    public static String getDiffFilePath() {
        return diffFilePath;
    }

    public static String getElementId() {
        return elementId;
    }
}
