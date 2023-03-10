package scroogecoin.main;

import java.util.Arrays;

public class UTXO implements Comparable<UTXO> {

    /** Hash of the transaction from which this scroogecoin.main.UTXO originates */
    private byte[] txHash;

    /** Index of the corresponding output in said transaction */
    private int index;

    /**
     * Creates a new scroogecoin.main.UTXO corresponding to the output with index <index> in the transaction whose
     * hash is {@code txHash}
     */
    public UTXO(byte[] txHash, int index) {
        this.txHash = Arrays.copyOf(txHash, txHash.length);
        this.index = index;
    }

    /** @return the transaction hash of this scroogecoin.main.UTXO */
    public byte[] getTxHash() {
        return txHash;
    }

    /** @return the index of this scroogecoin.main.UTXO */
    public int getIndex() {
        return index;
    }

    /**
     * Compares this scroogecoin.main.UTXO to the one specified by {@code other}, considering them equal if they have
     * {@code txHash} arrays with equal contents and equal {@code index} values
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }

        UTXO utxo = (UTXO) other;
        byte[] hash = utxo.txHash;
        int index = utxo.index;
        if (this.index != index) {
            return false;
        }
        return Arrays.equals(hash, txHash);
    }

    /**
     * Simple implementation of a scroogecoin.main.UTXO hashCode that respects equality of UTXOs // (i.e.
     * utxo1.equals(utxo2) => utxo1.hashCode() == utxo2.hashCode())
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + index;
        hash = hash * 31 + Arrays.hashCode(txHash);
        return hash;
    }

    /** Compares this scroogecoin.main.UTXO to the one specified by {@code utxo} */
    @Override
    public int compareTo(UTXO utxo) {
        byte[] hash = utxo.txHash;
        int index = utxo.index;
        if (index > this.index) {
            return -1;
        }
        else if (index < this.index) {
            return 1;
        }
        else {
            int len1 = txHash.length;
            int len2 = hash.length;
            if (len2 > len1) {
                return -1;
            }
            else if (len2 < len1) {
                return 1;
            }
            else {
                for (int i = 0; i < len1; i++) {
                    if (hash[i] > txHash[i]) {
                        return -1;
                    }
                    else if (hash[i] < txHash[i]) {
                        return 1;
                    }
                }
                return 0;
            }
        }
    }
}
