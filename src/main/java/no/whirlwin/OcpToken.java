package no.whirlwin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

final class OcpToken {

    private static final String K8S_SA_TOKEN_PATH = "/var/run/secrets/kubernetes.io/serviceaccount/token";

    private Optional<String> serviceAccountToken;
    private Optional<String> personalToken;

    void initialize() {
        serviceAccountToken = getServiceAccountTokenFromFile();
        personalToken = getWhoamiToken();
    }

    Optional<String> getServiceAccountToken() {
        return serviceAccountToken;
    }

    Optional<String> getPersonalToken() {
        return personalToken;
    }

    private Optional<String> getServiceAccountTokenFromFile() {
        try {
            return Optional.of(new String(Files.readAllBytes(Paths.get(K8S_SA_TOKEN_PATH)), Charset.defaultCharset()));
        } catch (final IOException ignored) {
            return Optional.empty();
        }
    }

    private Optional<String> getWhoamiToken() {
        try {
            final Process ocProcess = new ProcessBuilder("oc", "whoami", "--show-token").start();
            final String stderr = inputStreamToString(ocProcess.getErrorStream());
            final String stdout = inputStreamToString(ocProcess.getInputStream());

            if (!stdout.isEmpty() && stderr.isEmpty()) {
                return Optional.of(stdout.trim());
            }
        } catch (final IOException ignored) {

        }
        return Optional.empty();
    }

    private String inputStreamToString(final InputStream stream) {
        final InputStreamReader in = new InputStreamReader(stream, Charset.defaultCharset());
        final BufferedReader reader = new BufferedReader(in);
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    /*
    static {
        final String BEARER_VALUE_PREFIX = "Bearer ";
        try {
            AUTHORIZATION_HEADER_VALUE = BEARER_VALUE_PREFIX + new String(Files.readAllBytes(
                    Paths.get(K8S_SA_TOKEN_PATH)), Charset.defaultCharset());
        } catch (final NoSuchFileException e) {
            try {
                Process ocProcess = new ProcessBuilder("oc", "whoami", "--show-token").start();
                String stderr = IOUtils.toString(ocProcess.getErrorStream(), Charset.defaultCharset());
                String stdout = IOUtils.toString(ocProcess.getInputStream(), Charset.defaultCharset());

                if (!stdout.isEmpty() && stderr.isEmpty()) {
                    AUTHORIZATION_HEADER_VALUE = BEARER_VALUE_PREFIX + stdout.trim();
                } else {
                    throw new RuntimeException(String.format("Failed to get OCP token: %s - %s", stdout, stderr));
                }
            } catch (final IOException e1) {
                throw new RuntimeException(e);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }*/
    }
