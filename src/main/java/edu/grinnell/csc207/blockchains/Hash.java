package edu.grinnell.csc207.blockchains;

import java.util.Arrays;
import java.util.HexFormat;

/**
 * Encapsulated hashes.
 *
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
public class Hash {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Data stored as byte array.
   */
  byte[] hashData;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new encapsulated hash.
   *
   * @param data The data to copy into the hash.
   */
  public Hash(byte[] data) {
    this.hashData = data.clone();
  } // Hash(byte[])

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Determine how many bytes are in the hash.
   *
   * @return the number of bytes in the hash.
   */
  public int length() {
    return hashData.length;
  } // length()

  /**
   * Get the ith byte.
   *
   * @param i The index of the byte to get, between 0 (inclusive) and length() (exclusive).
   * @return the ith byte
   */
  public byte get(int i) {
    return hashData[i];
  } // get()

  /**
   * Get a copy of the bytes in the hash. We make a copy so that the client cannot change them.
   *
   * @return a copy of the bytes in the hash.
   */
  public byte[] getBytes() {
    return hashData.clone();
  } // getBytes()

  /**
   * Convert to a hex string.
   *
   * @return the hash as a hex string.
   */
  public String toString() {
    return HexFormat.of().formatHex(hashData).toUpperCase();
  } // toString()

  /**
   * Determine if this is equal to another object.
   *
   * @param other The object to compare to.
   * @return true if the two objects are conceptually equal and false otherwise.
   */
  public boolean equals(Object other) {
    if (other instanceof Hash) {
      return Arrays.equals(hashData, ((Hash) other).getBytes());
    }
    return false;
  } // equals(Object)

  /**
   * Get the hash code of this object.
   *
   * @return the hash code.
   */
  public int hashCode() {
    return this.toString().hashCode();
  } // hashCode()
} // class Hash
