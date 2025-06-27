package org.tma.intern.application;

import io.smallrye.mutiny.Multi;

public interface IdentityProviderClient {

    Multi<String> getRoles();

}
