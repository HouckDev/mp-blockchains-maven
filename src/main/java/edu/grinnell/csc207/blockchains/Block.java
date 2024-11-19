package edu.grinnell.csc207.blockchains;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
public class Block {

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
    this.num = num;
    this.transaction = transaction;
    this.preHash = prevHash;
    // STUB: Mine the nonce
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
    this.hash = this.calculateHash();
  } // computeHash()

  /**
   * Returns the calculates the has of the current block.
   *
   * @return computed Hash
   */
  public Hash calculateHash() {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

    byteStream.write((byte) num);
    byteStream.writeBytes(transaction.getBytes());
    byteStream.writeBytes(preHash.getBytes());
    byteStream.write((byte) nonce);

    if (previousBlock != null) {
      byteStream.writeBytes(previousBlock.getHash().getBytes());
    } // if

//  Using Message digest to produce hash
    try {
      MessageDigest md = MessageDigest.getInstance("sha-256");
      md.update(byteStream.toByteArray());
      return new Hash(md.digest());
    } catch (NoSuchAlgorithmException e) {
      // do nothing
    } // try-catch

    return null;
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
  Hash getPrevHash() {
    return this.preHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  Hash getHash() {
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
          "Block <num> (Transaction: [Deposit Target <target>, Amount: <amt>], Nonce: <nonce>, prevHash: <prevHash>, hash: <hash>)",
          getNum(), getTransaction().getTarget(),
          getTransaction().getAmount(), getNonce(), getPrevHash(), getHash());
    } else {
      return String.format(
          "Block <num> (Transaction: [Source: <source>, Target <target>, Amount: <amt>], Nonce: <nonce>, prevHash: <prevHash>, hash: <hash>)",
          getNum(), getTransaction().getSource(), getTransaction().getTarget(),
          getTransaction().getAmount(), getNonce(), getPrevHash(), getHash());
    }
  } // toString()
} // class Block
