package org.tma.intern.application.injection;

import java.util.List;

public interface IdentityContext {

    String getCurrentUser();

    String getRegion();

    String getAccessToken();

    List<String> getRoles();

    String getClaim(String key);

}
