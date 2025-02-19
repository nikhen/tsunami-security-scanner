/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.tsunami.plugin.payload;

import com.google.common.io.BaseEncoding;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.SecureRandom;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;

/** Generates secrets used in the Payload generation framework */
public final class PayloadSecretGenerator {

  private final SecureRandom payloadSecretRng;

  // This method is public so that BlindExploitGenerator can use it
  // TODO(b/205184738): Remove visiblity when BlindExploitGenerator is removed.
  @SuppressWarnings("UnnecessarilyVisible")
  @Inject
  public PayloadSecretGenerator(@PayloadSecretRng SecureRandom payloadSecretRng) {
    this.payloadSecretRng = payloadSecretRng;
  }

  public String generate(int secretLength) {
    byte[] randomBytes = new byte[secretLength];
    payloadSecretRng.nextBytes(randomBytes);
    return BaseEncoding.base16().lowerCase().encode(randomBytes);
  }

  public static Module getModule() {
    return new Module();
  }

  private static final class Module extends AbstractModule {
    @Provides
    @PayloadSecretRng
    @Singleton
    SecureRandom providesPayloadSecretRng() {
      return new SecureRandom();
    }
  }

  /**
   * Interface for Guice binding annotation for the {@link PayloadSecretGenerator}'s SecureRandom
   */
  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  public @interface PayloadSecretRng {}
}
