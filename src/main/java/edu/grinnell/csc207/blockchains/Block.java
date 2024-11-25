package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

/**
 * Blocks to be stored in blockchain.
 *
 * @author Grant Sackmann
 * @author Paden Houck
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Object holding block Transactions.
   */
  Transaction transaction;

  /**
   * Block Hash.
   */
  Hash hash;

  /**
   * The Hash of the previous Block.
   */
  Hash prevHash;

  /**
   * Hash validation object.
   */
  HashValidator check;

  /**
   * Number once— a randomly generated number for cryptographic security.
   */
  long nonce;

  /**
   * Reference to the previous block in the blockchain.
   */
  Block previousBlock;

  /**
   * Reference to the next block in the blockchain.
   */
  Block nextBlock;

  /**
   * the nth block in the blockchain sequence.
   */
  int num;

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
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, _transaction, and previous hash, mining to
   * choose a nonce that meets the requirements of the validator.
   *
   * @param pNum         The number of the block.
   * @param pTransaction The _transaction for the block.
   * @param pPrevHash    The hash of the previous block.
   * @param pCheck       The validator used to _check the block.
   */
  public Block(int pNum, Transaction pTransaction, Hash pPrevHash, HashValidator pCheck) {
    trySetup();
    this.num = pNum;
    this.transaction = pTransaction;
    this.prevHash = pPrevHash;
    this.nonce = 0;
    while (!pCheck.isValid(calculateHash())) {
      this.nonce += 1;
    } // while
    computeHash();

  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param pNum         The number of the block.
   * @param pTransaction The _transaction for the block.
   * @param pPrevHash    The hash of the previous block.
   * @param pNonce       The validator used to _check the block.
   */
  public Block(int pNum, Transaction pTransaction, Hash pPrevHash, long pNonce) {
    trySetup();
    this.num = pNum;
    this.transaction = pTransaction;
    this.prevHash = pPrevHash;
    this.nonce = pNonce;
    computeHash();
  } // Block(int, Transaction, Hash, long)

  // +----------------+------------------------------------------------
  // | Helper Methods |
  // +----------------+

  /**
   * Initializes static fields of class if not already done so.
   */
  static void trySetup() {
    if (md != null) {
      return;
    } // if
    try {
      Block.md = MessageDigest.getInstance("sha-256");
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchElementException("Cannot instantiate sha-256 Algorithm");
    } // try-catch
    Block.intBuffer = ByteBuffer.allocate(Integer.BYTES);
    Block.longBuffer = ByteBuffer.allocate(Long.BYTES);
  } // try-setup

  /**
   * Retrieves the previous block.
   *
   * @return Block reference
   */
  public Block getPreviousBlock() {
    return previousBlock;
  } // getPreviousBlock()

  /**
   * Sets the block's previous block to the provided block.
   *
   * @param pPreviousBlock Block
   */
  public void setPreviousBlock(Block pPreviousBlock) {
    if (this.previousBlock != null) {
      pPreviousBlock.nextBlock = null;
    } // if
    this.previousBlock = pPreviousBlock;
    if (this.previousBlock != null) {
      pPreviousBlock.nextBlock = this;
    } // if
  } // setPreviousBlock()

  /**
   * Retrieves the next block.
   *
   * @return Block reference
   */
  public Block getNextBlock() {
    return this.nextBlock;
  } // getNextBlock()

  /**
   * Sets this block's next block to the provided.
   *
   * @param pNextBlock Block
   */
  public void setNextBlock(Block pNextBlock) {
    if (this.nextBlock != null) {
      pNextBlock.previousBlock = null;
    } // if
    this.nextBlock = pNextBlock;
    if (this.nextBlock != null) {
      pNextBlock.previousBlock = this;
    } // if
  } // setNextBlock(Block)

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
    return this.prevHash;
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
          "Block %s (Transaction: [Deposit Target %s, Amount: %s], "
              + "Nonce: %s, prevHash: %s, hash: %s)",
          getNum(), getTransaction().getTarget(),
          getTransaction().getAmount(), getNonce(), getPrevHash(), getHash());
    } else {
      return String.format(
          "Block %s (Transaction: [Source: %s, Target %s, Amount: %s], "
              + "Nonce: %s, prevHash: %s, hash: %s)",
          getNum(), getTransaction().getSource(), getTransaction().getTarget(),
          getTransaction().getAmount(), getNonce(), getPrevHash(), getHash());
    } // if-else
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
