package no.whirlwin;

import java.util.Optional;

public final class OpenShiftTools {

    private final OcpToken ocpToken = new OcpToken();

    public void initialize() {
        ocpToken.initialize();
    }

    public Optional<String> getServiceAccountToken() {
        return ocpToken.getServiceAccountToken();
    }

    public Optional<String> getPersonalToken() {
        return ocpToken.getPersonalToken();
    }
}
