package edu.grinnell.csc207.main;

import edu.grinnell.csc207.blockchains.Block;
import edu.grinnell.csc207.blockchains.BlockChain;
import edu.grinnell.csc207.blockchains.Hash;
import edu.grinnell.csc207.blockchains.HashValidator;
import edu.grinnell.csc207.blockchains.Transaction;

import edu.grinnell.csc207.util.IOUtils;

import java.io.PrintWriter;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A simple UI for our BlockChain class.
 *
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
public class BlockChainUI {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The number of bytes we validate. Should be set to 3 before submitting.
   */
  static final int VALIDATOR_BYTES = 1;

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Print out the instructions.
   *
   * @param pen The pen used for printing instructions.
   */
  public static void instructions(PrintWriter pen) {
    pen.println("""
        Valid commands:
          mine: discovers the nonce for a given transaction
          append: appends a new block onto the end of the chain
          remove: removes the last block from the end of the chain
          check: checks that the block chain is valid
          users: prints a list of users
          balance: finds a user's balance
          transactions: prints out the chain of transactions
          blocks: prints out the chain of blocks (for debugging only)
          help: prints this list of commands
          quit: quits the program""");
  } // instructions(PrintWriter)

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the UI.
   *
   * @param args Command-line arguments (currently ignored).
   */
  public static void main(String[] args) throws Exception {
    PrintWriter pen = new PrintWriter(System.out, true);
    BufferedReader eyes = new BufferedReader(new InputStreamReader(System.in));

    // Set up our blockchain.
    HashValidator validator = (h) -> {
      if (h.length() < VALIDATOR_BYTES) {
        return false;
      } // if
      for (int v = 0; v < VALIDATOR_BYTES; v++) {
        if (h.get(v) != 0) {
          return false;
        } // if
      } // for
      return true;
    };
    BlockChain chain = new BlockChain(validator);

    instructions(pen);

    boolean done = false;

    String source;
    String target;
    int amount;
    int nonce;
    Block b;

    while (!done) {
      pen.print("\nCommand: ");
      pen.flush();
      String command = eyes.readLine();
      if (command == null) {
        command = "quit";
      } // if

      switch (command.toLowerCase()) {
        case "append":
          source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");
          nonce = IOUtils.readInt(pen, eyes, "Nonce: ");
          if (chain.getLastBlock() == null) {
            b = new Block(chain.getSize(), new Transaction(source, target, amount),
                new Hash(new byte[] {}), nonce);
          } else {
            b = new Block(chain.getSize(), new Transaction(source, target, amount),
                chain.getLastBlock().getHash(), nonce);
          }
          try {
            chain.append(b);
            pen.println("Appended: " + b.toString());
          } catch (Exception e) {
            pen.println("Could not append: Invalid hash in appended block: " + b.getHash().toString());
          }
          break;

        case "balance":
          source = IOUtils.readLine(pen, eyes, "User: ");
          pen.printf("> '%s' : '%s'", source, chain.balance(source));
          break;

        case "blocks":
          Iterator<Block> blockIterator = chain.blocks();
          pen.println("Blocks " + chain.getSize());
          while (blockIterator.hasNext()) {
            Block block = (Block) blockIterator.next();
            pen.println(block.toString());
          }
          break;

        case "check":
          try {
            chain.check();
            pen.println("The blockchain checks out.");
          } catch (Exception e) {
            pen.println("The blockchain does not check out.");
          }
          break;

        case "help":
          instructions(pen);
          break;

        case "mine":
          source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");
          b = chain.mine(new Transaction(source, target, amount));
          pen.println("Nonce: " + b.getNonce());
          break;

        case "quit":
          done = true;
          break;

        case "remove":
          chain.removeLast();
          break;

        case "transactions":
          Iterator<Block> transactionIterator = chain.blocks();
          pen.printf("Transactions");
          while (transactionIterator.hasNext()) {
            Block block = (Block) transactionIterator.next();
            pen.printf("> '%s' : '%s'", 0, block.getTransaction().toString());
          }
          break;

        case "users":
          Iterator<String> userIterator = chain.users();
          pen.printf("Users");
          while (userIterator.hasNext()) {
            String cUser = (String) userIterator.next();
            pen.printf("> '%s' : '%s'", cUser, chain.balance(cUser));
          }
          break;

        default:
          pen.printf("invalid command: '%s'. Try again.\n", command);
          break;
      } // switch
    } // while

    pen.printf("\nGoodbye\n");
    eyes.close();
    pen.close();
  } // main(String[])
} // class BlockChainUI
