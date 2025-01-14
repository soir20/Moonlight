import com.google.common.graph.Network;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mehvahdjukaar.moonlight.api.client.IScreenProvider;
import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.network.ClientBoundOpenScreenPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public class ChannelHandlerExample {

    // Just loads the class
    public static void init(){
    }

    // Showcasing usage of network handler
    public static void sendMessageToPlayers(Level level, BlockPos pos, int range, int data){
        CHANNEL.sendToAllClientPlayersInRange(level, pos, range, new S2CTestMessage(data));
    }

    private static final ChannelHandler CHANNEL;

    static {
        CHANNEL = ChannelHandler.createChannel(Moonlight.res("main_channel"));

        CHANNEL.register(NetworkDir.PLAY_TO_CLIENT, S2CTestMessage.class, S2CTestMessage::new);
    }

    public record S2CTestMessage(int data) implements Message{

        public S2CTestMessage(FriendlyByteBuf buffer) {
            this(buffer.readVarInt());
        }

        @Override
        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeVarInt(this.data);
        }

        @Override
        public void handle(ChannelHandler.Context context) {
            // Handle your packet on the client here
        }
    }
}
