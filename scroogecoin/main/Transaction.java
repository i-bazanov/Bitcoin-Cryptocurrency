package scroogecoin.main;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class Transaction {

    public class Input {
        /** hash of the scroogecoin.main.Transaction whose output is being used */
        public byte[] prevTxHash;
        /** used output's index in the previous transaction */
        public int outputIndex;
        /** the signature produced to check validity */
        public byte[] signature;

        public Input(byte[] prevHash, int index) {
            if (prevHash == null) {
                prevTxHash = null;
            }
            else {
                prevTxHash = Arrays.copyOf(prevHash, prevHash.length);
            }
            outputIndex = index;
        }

        public void addSignature(byte[] sig) {
            if (sig == null) {
                signature = null;
            }
            else {
                signature = Arrays.copyOf(sig, sig.length);
            }
        }
    }

    public class Output {
        /** value in bitcoins of the output */
        public double value;
        /** the address or public key of the recipient */
        public PublicKey address;

        public Output(double v, PublicKey addr) {
            value = v;
            address = addr;
        }
    }

    /** hash of the transaction, its unique id */
    private byte[] hash;
    private ArrayList<Input> inputs = null;
    private ArrayList<Output> outputs = null;

    public Transaction() {
        inputs = new ArrayList<Input>();
        outputs = new ArrayList<Output>();
    }

    public Transaction(Transaction tx) {
        hash = tx.hash.clone();
        inputs = new ArrayList<Input>(tx.inputs);
        outputs = new ArrayList<Output>(tx.outputs);
    }

    public void addInput(byte[] prevTxHash, int outputIndex) {
        Input in = new Input(prevTxHash, outputIndex);
        inputs.add(in);
    }

    public void addOutput(double value, PublicKey address) {
        Output op = new Output(value, address);
        outputs.add(op);
    }

    public void removeInput(int index) {
        inputs.remove(index);
    }

    public void removeInput(UTXO ut) {
        for (Input in: inputs) {
            UTXO u = new UTXO(in.prevTxHash, in.outputIndex);
            if (u.equals(ut)) {
                inputs.remove(in);
                return;
            }
        }
    }

    public byte[] getRawDataToSign(int index) {
        // ith input and all outputs
        if (index > inputs.size()) {
            return null;
        }
        ArrayList<Byte> sigData = new ArrayList<Byte>();
        Input in = inputs.get(index);

        byte[] prevTxHash = in.prevTxHash;
        byte[] outputIndex = ByteBuffer.allocate(Integer.BYTES)
                .putInt(in.outputIndex)
                .array();

        if (prevTxHash != null) {
            for(byte _byte: prevTxHash) {
                sigData.add(_byte);
            }
        }
        for(byte _byte: outputIndex) {
            sigData.add(_byte);
        }
        for (Output op : outputs) {
            byte[] value = ByteBuffer.allocate(Double.BYTES)
                    .putDouble(op.value)
                    .array();
            byte[] addressBytes = op.address.getEncoded();

            for(byte _byte: value) {
                sigData.add(_byte);
            }
            for(byte _byte: addressBytes) {
                sigData.add(_byte);
            }
        }

        byte[] sigD = new byte[sigData.size()];
        int i = 0;
        for (Byte sb : sigData) {
            sigD[i++] = sb;
        }
        return sigD;
    }

    public void addSignature(byte[] signature, int index) {
        inputs.get(index).addSignature(signature);
    }

    public byte[] getRawTx() {
        ArrayList<Byte> rawTx = new ArrayList<Byte>();

        for (Input in : inputs) {
            byte[] prevTxHash = in.prevTxHash;
            byte[] outputIndex = ByteBuffer.allocate(Integer.BYTES)
                    .putInt(in.outputIndex)
                    .array();
            byte[] signature = in.signature;

            if (prevTxHash != null) {
                for(byte _byte: prevTxHash) {
                    rawTx.add(_byte);
                }
            }
            for(byte _byte: outputIndex) {
                rawTx.add(_byte);
            }
            if (signature != null) {
                for(byte _byte: signature) {
                    rawTx.add(_byte);
                }
            }
        }
        for (Output op : outputs) {
            byte[] value = ByteBuffer.allocate(Double.BYTES)
                    .putDouble(op.value)
                    .array();
            byte[] addressBytes = op.address.getEncoded();

            for(byte _byte: value) {
                rawTx.add(_byte);
            }
            for(byte _byte: addressBytes) {
                rawTx.add(_byte);
            }
        }

        byte[] tx = new byte[rawTx.size()];
        int i = 0;
        for (Byte b : rawTx) {
            tx[i++] = b;
        }
        return tx;
    }

    public void finalize() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(getRawTx());
            hash = md.digest();
        } catch (NoSuchAlgorithmException x) {
            x.printStackTrace(System.err);
        }
    }

    public void setHash(byte[] h) {
        hash = h;
    }

    public byte[] getHash() {
        return hash;
    }

    public ArrayList<Input> getInputs() {
        return inputs;
    }

    public ArrayList<Output> getOutputs() {
        return outputs;
    }

    public Input getInput(int index) {
        if (index < inputs.size()) {
            return inputs.get(index);
        }
        return null;
    }

    public Output getOutput(int index) {
        if (index < outputs.size()) {
            return outputs.get(index);
        }
        return null;
    }

    public int numInputs() {
        return inputs.size();
    }

    public int numOutputs() {
        return outputs.size();
    }
}