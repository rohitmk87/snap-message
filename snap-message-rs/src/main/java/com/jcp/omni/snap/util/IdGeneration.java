package com.jcp.omni.snap.util;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.utils.UUIDs;
import java.util.Base64;
import java.util.UUID;

/**
 * Generate timebased random id
 */
@Component
public class IdGeneration {

  public static String generateUniqueId() {
    String id;
    do {
      id = doGenerateShortUniqueId();
    } while (id.contains("_"));
    return id;
  }

  private static String doGenerateShortUniqueId() {
    UUID uuid = UUIDs.timeBased();

    HashCode hash = Hashing.murmur3_128().newHasher()
        .putLong(uuid.getMostSignificantBits())
        .putLong(uuid.getLeastSignificantBits())
        .hash();

    byte[] bytes = new byte[15];
    hash.writeBytesTo(bytes, 0, 15);
    return Base64.getUrlEncoder().encodeToString(bytes);
  }

}
