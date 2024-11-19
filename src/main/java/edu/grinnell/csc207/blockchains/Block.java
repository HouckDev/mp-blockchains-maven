package edu.grinnell.csc207.blockchains;

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
    if (this.previousBlock != null) {previousBlock.nextBlock = null;}
    this.previousBlock = previousBlock;
    if (this.previousBlock != null) {previousBlock.nextBlock = this;}
  }

  Block nextBlock;

  public Block getNextBlock() {
    return nextBlock;
  }

  public void setNextBlock(Block nextBlock) {
    if (this.nextBlock != null) {nextBlock.previousBlock = null;}
    this.nextBlock = nextBlock;
    if (this.nextBlock != null) {nextBlock.previousBlock = this;}
  }
  
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and
   * previous hash, mining to choose a nonce that meets the requirements
   * of the validator.
   *
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param check
   *   The validator used to check the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash,
      HashValidator check) {
    this.num = num;
    this.transaction = transaction;
    this.preHash = prevHash;
    // STUB: Mine the nonce
    computeHash();
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param nonce
   *   The nonce of the block.
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
   * Compute the hash of the block given all the other info already
   * stored in the block.
   */
  static void computeHash() {
    // STUB
  } // computeHash()

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
    return "";  // STUB
  } // toString()
} // class Block
