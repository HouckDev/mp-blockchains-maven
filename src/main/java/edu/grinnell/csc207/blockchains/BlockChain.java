package edu.grinnell.csc207.blockchains;

import java.util.HashMap;
import java.util.Iterator;


/**
 * A full blockchain.
 *
 * @author Paden Houck
 * @author Grant Sackmann
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The head Block of the blockchain.
   */
  Block rootBlock;

  /**
   * Validator object to check blockchain.
   */
  HashValidator check;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+


  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param checker The validator used to check elements.
   */
  public BlockChain(HashValidator checker) {
    this.check = checker;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Retrieves the last block of the blockchain.
   *
   * @return Block
   */
  public Block getLastBlock() {
    Block current = this.rootBlock;

    while (current != null && current.getNextBlock() != null) {
      current = current.getNextBlock();
    } // while

    return current;
  } // getLastBlock()
  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that block.
   *
   * @param t The transaction that goes in the block.
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t) {
    if (getLastBlock() == null) {
      return new Block(getSize(), t, new Hash(new byte[]{}), check);
    } // if
    return new Block(getSize(), t, getLastBlock().getHash(), check);
  } // mine(Transaction)

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    if (this.rootBlock == null) {
      return 0;
    } // if
    Block current = this.rootBlock;
    int count = 1;
    while (current.getNextBlock() != null) {
      current = current.getNextBlock();
      count++;
    } // while
    return count;
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk The block to add to the end of the chain.
   * @throws IllegalArgumentException if (a) the hash is not valid, (b) the hash is not appropriate
   *                                  for the contents, or (c) the previous hash is incorrect.
   */
  public void append(Block blk) {
    if (getLastBlock() != null && !blk.getPrevHash().equals(getLastBlock().getHash())) {
      throw new IllegalArgumentException();
    } // if
    if (!blk.getHash().equals(blk.calculateHash())) {
      throw new IllegalArgumentException();
    } // if
    if (getLastBlock() == null) {
      rootBlock = blk;
    } else {
      getLastBlock().setNextBlock(blk);
    } // if
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's not removed) or true
   * otherwise (in which case the last block is removed).
   */
  public boolean removeLast() {
    if (this.rootBlock == this.getLastBlock()) {
      return false;
    } // if
    this.getLastBlock().getPreviousBlock().setNextBlock(null);
    return true;
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return getLastBlock().getHash();
  } // getHash()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @return true if the blockchain is correct and false otherwise.
   */
  public boolean isCorrect() {
    Iterator<Block> blockIterator = this.blocks();
    HashMap<String, Integer> runningBalances = this.calculateBalances();
    while (blockIterator.hasNext()) {
      Block block = blockIterator.next();
      if (block.getPreviousBlock() != null
          && block.getPrevHash() != block.getPreviousBlock().getHash()) {
        return false;
      } // if
      if (block.getPreviousBlock() != null
          && block.getHash() != block.calculateHash()) {
        return false;
      } // if
      if (runningBalances.get(block.getTransaction().getTarget()) < 0
          || runningBalances.get(block.getTransaction().getSource()) < 0) {
        return false;
      } // if
    } // while
    return true;
  } // isCorrect()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception If things are wrong at any block.
   */
  public void check() throws Exception {
    if (!isCorrect()) {
      throw new Exception();
    } // if
  } // check()

  /**
   * Return an iterator of all the people who participated in the system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    return new Iterator<String>() {
      int i = 0;
      Object[] userList = calculateBalances().keySet().toArray();

      public boolean hasNext() {
        return i < userList.length;
      } // hasNext()

      public String next() {
        String temp = (String) userList[i];
        i++;
        return temp;
      } // next()
    };
  } // users()

  /**
   * Find one user's balance.
   *
   * @param user The user whose balance we want to find.
   * @return that user's balance (or 0, if the user is not in the system).
   */
  public int balance(String user) {
    if (calculateBalances().containsKey(user)) {
      return calculateBalances().get(user);
    } // if
    return 0;
  } // balance()

  /**
   * Calculates the final balances users in the blockchain.
   *
   * @return HashMap of String usernames to balances
   */
  public HashMap<String, Integer> calculateBalances() {
    HashMap<String, Integer> runningBalances = new HashMap<String, Integer>();
    Iterator<Block> blockIterator = this.blocks();
    while (blockIterator.hasNext()) {
      Block block = blockIterator.next();
      if (block.getTransaction().getSource().isEmpty()) {
        runningBalances.put(block.getTransaction().getTarget(),
            runningBalances.get(block.getTransaction().getTarget())
                + block.getTransaction().getAmount());
      } else {
        runningBalances.put(block.getTransaction().getTarget(),
            runningBalances.get(block.getTransaction().getTarget())
                + block.getTransaction().getAmount());
        runningBalances.put(block.getTransaction().getSource(),
            runningBalances.get(block.getTransaction().getSource())
                - block.getTransaction().getAmount());
      } // if else
    } // while
    return runningBalances;
  } // calculateBalances

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      Block currentBlock = rootBlock;

      public boolean hasNext() {
        return currentBlock != null;
      } // hasNext()

      public Block next() {
        Block temp = currentBlock;
        currentBlock = currentBlock.getNextBlock();
        return temp;
      } // next()
    };
  } // blocks()

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      Block currentBlock = rootBlock;

      public boolean hasNext() {
        return currentBlock.getNextBlock() != null;
      } // hasNext()

      public Transaction next() {
        Block temp = currentBlock;
        currentBlock = currentBlock.getNextBlock();
        return temp.getTransaction();
      } // next()
    };
  } // iterator()

} // class BlockChain
