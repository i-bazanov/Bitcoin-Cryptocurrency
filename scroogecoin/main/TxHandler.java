package scroogecoin.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class TxHandler {

    private UTXOPool claimedUTXOPool;

    /**
     * Creates a public ledger whose current scroogecoin.main.UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the scroogecoin.main.UTXOPool(scroogecoin.main.UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.claimedUTXOPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current scroogecoin.main.UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid,
     * (3) no scroogecoin.main.UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        //1
        for(Transaction.Input input: tx.getInputs()) {
            UTXO claimedUTXO = new UTXO(input.prevTxHash, input.outputIndex);
            if(!claimedUTXOPool.contains(claimedUTXO)) {
                return false;
            }
        }

        //2
        for(int i = 0; i < tx.numInputs(); ++i) {
            Transaction.Input input = tx.getInput(i);
            UTXO claimedUTXO = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output output = claimedUTXOPool.getTxOutput(claimedUTXO);

            if(!Crypto.verifySignature(output.address, tx.getRawDataToSign(i), input.signature)) {
                return false;
            }
        }

        //3
        Set<UTXO> utxoSet = new HashSet<>();

        for(Transaction.Input input: tx.getInputs()) {
            UTXO claimedUTXO = new UTXO(input.prevTxHash, input.outputIndex);
            if(utxoSet.contains(claimedUTXO)) {
                return false;
            }
            utxoSet.add(claimedUTXO);
        }

        //4
        for(Transaction.Output output: tx.getOutputs()) {
            if(Math.signum(output.value) == -1.0) {
                return false;
            }
        }

        //5
        double sumInputs = 0.0;
        for(Transaction.Input input: tx.getInputs()) {
            UTXO claimedUTXO = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output output = claimedUTXOPool.getTxOutput(claimedUTXO);
            sumInputs += output.value;
        }

        double sumOutputs = 0.0;
        for(Transaction.Output output: tx.getOutputs()) {
            sumOutputs += output.value;
        }

        if(Math.signum(sumInputs - sumOutputs) == -1.0) {
            return false;
        }

        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current scroogecoin.main.UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        ArrayList<Transaction> rawCorrectTxs = new ArrayList<>();

        for(Transaction tx: possibleTxs) {
            if(isValidTx(tx)) {
                rawCorrectTxs.add(tx);
                updateUTXOPool(tx);
            }
        }

        Transaction[] correctTxs = Arrays.stream(rawCorrectTxs.toArray())
                                        .map(o -> (Transaction) o)
                                        .toArray(Transaction[]::new);

        return correctTxs;
    }

    private void updateUTXOPool(Transaction tx) {
        for (int i = 0; i < tx.numOutputs(); ++i) {
            UTXO claimedUTXO = new UTXO(tx.getHash(), i);
            claimedUTXOPool.addUTXO(claimedUTXO, tx.getOutput(i));
        }

        for (int i = 0; i < tx.numInputs(); ++i) {
            Transaction.Input input = tx.getInput(i);
            UTXO claimedUTXO = new UTXO(input.prevTxHash, input.outputIndex);
            claimedUTXOPool.removeUTXO(claimedUTXO);
        }
    }
}
