package http.server.externalapis;

public abstract class ExternalApiUrl {

    protected StringBuilder queryParams = new StringBuilder();
    private Boolean isFirstFilter = queryParams.isEmpty();
    protected final String baseUrl;

    public ExternalApiUrl(String baseUrl) {
        this.baseUrl = baseUrl;

    }

    protected void appendQueryParam(String key, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (isFirstFilter) {
            queryParams.append("?");
            isFirstFilter = false;
        } else {
            queryParams.append("&");
        }
        queryParams.append(key).append("=").append(value);
    }

    protected void appendValue(String value) {
        queryParams.append(value);
    }


    public String buildString() {
        return baseUrl + queryParams.toString();
    }

}
