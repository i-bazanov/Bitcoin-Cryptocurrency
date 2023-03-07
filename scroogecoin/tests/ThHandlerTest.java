package scroogecoin.tests;

import org.junit.Test;
import scroogecoin.main.Transaction;
import scroogecoin.main.TxHandler;
import scroogecoin.main.UTXOPool;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ThHandlerTest {

    private byte[] makeTestTxHash(int index) {
        ArrayList<Byte> rawTxHash = new ArrayList<Byte>();
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.SIZE / 8);
        byteBuffer.putInt(index);
        byte[] outputIndex = byteBuffer.array();
        for (int i = 0; i < outputIndex.length; i++)
            rawTxHash.add(outputIndex[i]);

        byte[] txHash = new byte[rawTxHash.size()];
        int i = 0;
        for (Byte sb : rawTxHash)
            txHash[i++] = sb;

        return txHash;
    }
    @Test
    public void checkDoubleSpending() {
        Transaction tx1 = new Transaction();

        int index = 1;
        tx1.addInput(makeTestTxHash(index), index);

        index = 2;
        tx1.addInput(makeTestTxHash(index), index);

        index = 3;
        tx1.addInput(makeTestTxHash(index), index);


        //Transaction tx2 = new Transaction(tx1);

        //Transaction[] txs = {tx1, tx2};

        //UTXOPool utxoPool = new UTXOPool();
        //TxHandler txHandler = new TxHandler(utxoPool);

        //assertThat(1, is(equalTo(txHandler.handleTxs(txs).length)));

    }
}
