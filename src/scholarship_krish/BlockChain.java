package scholarship_krish;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Krish
 */
public class BlockChain
{
    public static List<Block> block_chain;
    
    public BlockChain()
    {
        BlockChain.block_chain = new ArrayList<>();
    }
    
    public void addBlock(Block block)
    {
        BlockChain.block_chain.add(block);
    }
    
    public int getBlockChainSize()
    {
        return BlockChain.block_chain.size();
    }
    
    public List<Block> getBlockChain()
    {
        return BlockChain.block_chain;
    }

    @Override
    public String toString()
    {
        String temp_block_chain = "";
        for(Block block: this.block_chain)
        {
            temp_block_chain = temp_block_chain + block.toString() + "\n";
        }
        return temp_block_chain;
    }
}
