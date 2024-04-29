package com.ruse.net.login;

import com.ruse.GameSettings;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.entity.character.player.PlayerHandler;
import com.ruse.net.PlayerSession;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.util.NameUtils;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.*;

import static com.ruse.net.GameHandler.SESSION_KEY;

public class AuthenticationService {

    private static final ExecutorService thread = Executors.newSingleThreadExecutor();
    public static final BlockingQueue<Object> queue = new LinkedBlockingDeque<>(GameSettings.LOGIN_THRESHOLD);

    public static void addToQueue(Object message) {
        boolean ignore = queue.offer(message);
    }

    public static void start() {
        thread.submit(() -> {
            for(;;) {
                Object msg = queue.poll();
                if(msg instanceof LoginDetailsMessage loginDetailsMessage) {
                    login(loginDetailsMessage);
                } else if(msg instanceof Player player) {
                    PlayerHandler.handleLogout(player, false);
                }
            }
        });
    }

    public static void login(LoginDetailsMessage login) {
        Channel channel = login.getChannel();
        PlayerSession session = new PlayerSession(channel);
        Player player = new Player(session).setUsername(login.getUsername())
                .setLongUsername(NameUtils.stringToLong(login.getUsername()))
                .setPassword(login.getPassword())
                .setHostAddress(login.getHost())
                .setMac(login.getMac())
                .setUUID(login.getUUID());

        if (World.getPlayerByName(player.getUsername()) != null) {
            System.out.println("STOPPED MULTI LOG by "+player.getUsername());
            PlayerLogs.log(player.getUsername(), "Had a multilog attempt.");
            sendReturnCode(channel, LoginResponses.LOGIN_ACCOUNT_ONLINE);
            return;
        }

        session.setPlayer(player);

        int response = LoginResponses.getResponse(player, login);

        final boolean newAccount = response == LoginResponses.NEW_ACCOUNT;
        if(newAccount) {
            player.setNewPlayer(true);
            response = LoginResponses.LOGIN_SUCCESSFUL;
        }

        if (response == LoginResponses.LOGIN_SUCCESSFUL) {
            channel.write(new PacketBuilder().put((byte)2).put((byte)player.getRights().ordinal()).put((byte)0).toPacket());
            channel.attr(SESSION_KEY).set(session);
            PlayerHandler.handleLogin(player);
        } else {
            sendReturnCode(channel, response);
        }
    }

    public static void sendReturnCode(final Channel channel, final int code) {
        ChannelFuture future = channel.writeAndFlush(new PacketBuilder().put((byte) code).toPacket());
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                future1.channel().close();
            }
        });
    }
}
