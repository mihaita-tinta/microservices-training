package com.mih.training.invoice.wiremock;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Response;
import wiremock.org.eclipse.jetty.server.Request;

import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StubResponseTransformerWithParams extends ResponseTransformer {
    private static final Pattern interpolationPattern = Pattern.compile("\\$\\(.*?\\)");
    private static final Pattern randomIntegerPattern = Pattern.compile("!RandomInteger");
    private static final Pattern randomDoublePattern = Pattern.compile("!RandomDouble");

    @Override
    public String getName() {
        return "stub-transformer-with-params";
    }

    @Override
    public Response transform(com.github.tomakehurst.wiremock.http.Request request, Response response, FileSource files, Parameters parameters) {

        // Update the map with query parameters if any (if same names - replace)
        Map<String, Object> object = null;

        if (parameters != null) {
            String urlRegex = parameters.getString("urlRegex");

            if (urlRegex != null) {
                Pattern p = Pattern.compile(urlRegex);
                Matcher m = p.matcher(request.getUrl());

                // There may be more groups in the regex than the number of named capturing groups
                List<String> groups = getNamedGroupCandidates(urlRegex);

                if (m.matches() &&
                        groups.size() > 0 &&
                        groups.size() <= m.groupCount()) {

                    for (int i = 0; i < groups.size(); i++) {

                        if (object == null) {
                            object = new HashMap<>();
                        }

                        object.put(groups.get(i), m.group(i + 1));
                    }
                }
            }
        }

        if (response.getStatus() == 200) {
            return Response.Builder.like(response)
                    .but().body(transformResponse(object, response.getBodyAsString()))
                    .build();
        }

        return Response.Builder.like(response)
                .build();
    }

    private String transformResponse(Map requestObject, String response) {
        String modifiedResponse = response;

        Matcher matcher = interpolationPattern.matcher(response);
        while (matcher.find()) {
            String group = matcher.group();
            modifiedResponse = modifiedResponse.replace(group, getValue(group, requestObject));

        }

        return modifiedResponse;
    }
    private CharSequence getValue(String group, Map requestObject) {
        if (randomIntegerPattern.matcher(group).find()) {
            return String.valueOf(new Random().nextInt(2147483647));
        }
        if (randomDoublePattern.matcher(group).find()) {
            return String.valueOf(new Random().nextDouble() * 10000);
        }
        return getValueFromRequestObject(group, requestObject);
    }
    private CharSequence getValueFromRequestObject(String group, Map requestObject) {
        String fieldName = group.substring(2, group.length() - 1);
        String[] fieldNames = fieldName.split("\\.");
        Object tempObject = requestObject;
        for (String field : fieldNames) {
            if (tempObject instanceof Map) {
                tempObject = ((Map) tempObject).get(field);
            }
        }
        return String.valueOf(tempObject);
    }

    private static List<String> getNamedGroupCandidates(String regex) {
        List<String> namedGroups = new ArrayList<>();

        Matcher m = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*?)>").matcher(regex);

        while (m.find()) {
            namedGroups.add(m.group(1));
        }

        return namedGroups;
    }

}
