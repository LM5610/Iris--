package com.volmit.iris.generator.decorator;

import com.volmit.iris.Iris;
import com.volmit.iris.object.DecorationPart;
import com.volmit.iris.object.IrisBiome;
import com.volmit.iris.object.IrisDecorator;
import com.volmit.iris.scaffold.cache.Cache;
import com.volmit.iris.scaffold.engine.Engine;
import com.volmit.iris.scaffold.hunk.Hunk;
import org.bukkit.block.data.BlockData;

public class IrisSeaFloorDecorator extends IrisEngineDecorator
{
    public IrisSeaFloorDecorator(Engine engine) {
        super(engine, "Sea Floor", DecorationPart.SEA_FLOOR);
    }

    @Override
    public void decorate(int x, int z, int realX, int realX1, int realX_1, int realZ, int realZ1, int realZ_1, Hunk<BlockData> data, IrisBiome biome, int height, int max) {
        if(height <= getDimension().getFluidHeight()) {

            IrisDecorator decorator = getDecorator(biome, realX, realZ);

            if(decorator != null)
            {
                if(!decorator.isStacking())
                {
                    data.set(x, height, z, decorator.getBlockData100(biome, getRng(), realX, realZ, getData()));
                }
                else
                {
                    int stack = decorator.getHeight(getRng().nextParallelRNG(Cache.key(realX, realZ)), realX, realZ, getData());
                    stack = Math.min(stack, getDimension().getFluidHeight() - height + 2);
                    //Iris.info("Stack at " + realX + "," + realZ + " is " + stack);
                    BlockData top = decorator.getBlockDataForTop(biome, getRng(), realX, realZ, getData());
                    BlockData fill = decorator.getBlockData100(biome, getRng(), realX, realZ, getData());
                    for(int i = 0; i < stack; i++)
                    {
                        data.set(x, height+i, z, i == stack-1 ? top : fill);
                    }
                }
            }
        }
    }
}
