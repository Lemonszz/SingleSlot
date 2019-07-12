package party.lemons.singleinv;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenInventory implements IMessage
{
	public MessageOpenInventory()
	{
	}

	@Override
	public void toBytes(final ByteBuf buf)
	{
	}

	@Override
	public void fromBytes(final ByteBuf buf)
	{
	}

	public static class Handler implements IMessageHandler<MessageOpenInventory, IMessage>
	{

		@Override
		public IMessage onMessage(final MessageOpenInventory message, final MessageContext ctx)
		{
			final EntityPlayerMP player = ctx.getServerHandler().player;
			final WorldServer world = player.getServerWorld();

			world.addScheduledTask(() -> player.openGui(SingleInv.INSTANCE, 0, world, 0, 0, 0));
			return null;
		}
	}
}
