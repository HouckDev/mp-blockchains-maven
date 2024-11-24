package edu.grinnell.csc207.blockchains;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
public class Block {

  /**
   * The message digest used to compute hashes.
   */
  static MessageDigest md = null;

  /**
   * The byte buffer used for ints.
   */
  static ByteBuffer intBuffer = null;

  /**
   * The byte buffer used for longs.
   */
  static ByteBuffer longBuffer = null;

  static void trySetup() {
    if (md != null) {return;}
    try {
      Block.md = MessageDigest.getInstance("sha-256");
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchElementException("Cannot instantiate sha-256");
    } // try-catch
    Block.intBuffer = ByteBuffer.allocate(Integer.BYTES);
    Block.longBuffer = ByteBuffer.allocate(Long.BYTES);
  }
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  // Meta data
  int num;

  // Block data
  Transaction transaction;
  Hash hash;
  Hash preHash;
  HashValidator check;
  long nonce;

  // Connection data
  Block previousBlock;

  public Block getPreviousBlock() {
    return previousBlock;
  }

  public void setPreviousBlock(Block previousBlock) {
    if (this.previousBlock != null) {
      previousBlock.nextBlock = null;
    }
    this.previousBlock = previousBlock;
    if (this.previousBlock != null) {
      previousBlock.nextBlock = this;
    }
  }

  Block nextBlock;

  public Block getNextBlock() {
    return nextBlock;
  }

  public void setNextBlock(Block nextBlock) {
    if (this.nextBlock != null) {
      nextBlock.previousBlock = null;
    }
    this.nextBlock = nextBlock;
    if (this.nextBlock != null) {
      nextBlock.previousBlock = this;
    }
  }


  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and previous hash, mining to
   * choose a nonce that meets the requirements of the validator.
   *
   * @param num         The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash    The hash of the previous block.
   * @param check       The validator used to check the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, HashValidator check) {
    trySetup();

    this.num = num;
    this.transaction = transaction;
    this.preHash = prevHash;
    this.nonce = 0;
    // STUB: Mine the nonce
    while (!check.isValid(calculateHash())) {
      this.nonce += 1;
    }
    computeHash();

  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num         The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash    The hash of the previous block.
   * @param nonce       The nonce of the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, long nonce) {
    trySetup();

    this.num = num;
    this.transaction = transaction;
    this.preHash = prevHash;
    this.nonce = nonce;
    computeHash();
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already stored in the block.
   */
  public void computeHash() {
    this.hash = calculateHash();
  } // computeHash()

  /**
   * Returns the calculates the has of the current block.
   *
   * @return computed Hash
   */
  public Hash calculateHash() {
    Block.md.reset();
    Block.md.update(intToBytes(this.getNum()));
    Block.md.update(this.getTransaction().getSource().getBytes());
    Block.md.update(this.getTransaction().getTarget().getBytes());
    Block.md.update(intToBytes(this.getTransaction().getAmount()));
    Block.md.update(this.getPrevHash().getBytes());
    Block.md.update(longToBytes(this.getNonce()));
    return new Hash(Block.md.digest());
  } // calculateHash()

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return this.num;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return this.transaction;
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonce;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  public Hash getPrevHash() {
    return this.preHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  public Hash getHash() {
    return this.hash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    if (getTransaction().getSource().isEmpty()) {
      return String.format(
          "Block %s (Transaction: [Deposit Target %s, Amount: %s], Nonce: %s, prevHash: %s, hash: %s)",
          getNum(), getTransaction().getTarget(),
          getTransaction().getAmount(), getNonce(), getPrevHash(), getHash());
    } else {
      return String.format(
          "Block %s (Transaction: [Source: %s, Target %s, Amount: %s], Nonce: %s, prevHash: %s, hash: %s)",
          getNum(), getTransaction().getSource(), getTransaction().getTarget(),
          getTransaction().getAmount(), getNonce(), getPrevHash(), getHash());
    }
  } // toString()

  /**
   * Convert an integer into its bytes.
   *
   * @param i The integer to convert.
   * @return The bytes of that integer.
   */
  static byte[] intToBytes(int i) {
    Block.intBuffer.clear();
    return Block.intBuffer.putInt(i).array();
  } // intToBytes(int)

  /**
   * Convert a long into its bytes.
   *
   * @param l The long to convert.
   * @return The bytes in that long.
   */
  static byte[] longToBytes(long l) {
    Block.longBuffer.clear();
    return Block.longBuffer.putLong(l).array();
  } // longToBytes()
} // class Block
