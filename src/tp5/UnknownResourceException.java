package tp5;

class UnknownResourceException extends RuntimeException {

    public UnknownResourceException(String resourceTypeName, String resourceName) {
        super(resourceTypeName + " " + resourceName + " does not exist");
    }
}
