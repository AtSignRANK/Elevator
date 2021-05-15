package io.github.elevator;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Elevator extends JavaPlugin implements Listener {
    private static final int MAX_HEIGHT = 256;
    private static final int PARTICLE = 5;
    private static final Particle PARTICLE_TYPE = Particle.EXPLOSION_NORMAL;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    public Location MakeLocation(int x, int y, int z, World w) {
        return new Location(w, x, y, z);
    }

    public int Down(int x, int y, int z, World w) {
        int i;
        for(i=y-1;i>=1;i--) {
            Block block = MakeLocation(x, i, z, w).getBlock();
            if(IsBlockSign(block)) {
                Sign sign = (Sign) block.getState();
                if(sign.getLine(0).trim().equals("[엘리베이터]")) return y - i;
            }
        }
        return -1;
    }

    public int Up(int x, int y, int z, World w) {
        int i;
        for(i=y+1;i<=MAX_HEIGHT;i++) {
            Block block = MakeLocation(x, i, z, w).getBlock();
            if(IsBlockSign(block)) {
                Sign sign = (Sign) block.getState();
                if(sign.getLine(0).trim().equals("[엘리베이터]")) return i - y;
            }
        }
        return -1;
    }

    public boolean IsBlockSign(Block block) {
        return Objects.requireNonNull(block).getType().equals(Material.OAK_WALL_SIGN)
                || Objects.requireNonNull(block).getType().equals(Material.ACACIA_WALL_SIGN)
                || Objects.requireNonNull(block).getType().equals(Material.JUNGLE_WALL_SIGN)
                || Objects.requireNonNull(block).getType().equals(Material.SPRUCE_WALL_SIGN)
                || Objects.requireNonNull(block).getType().equals(Material.BIRCH_WALL_SIGN)
                || Objects.requireNonNull(block).getType().equals(Material.DARK_OAK_WALL_SIGN)
                || Objects.requireNonNull(block).getType().equals(Material.WARPED_WALL_SIGN)
                || Objects.requireNonNull(block).getType().equals(Material.CRIMSON_WALL_SIGN);
    }

    public boolean IsStandEmpty(Block block, Player player) {
        return block.getLocation().getX() - 0.3 < player.getLocation().getX() && block.getLocation().getX() + 0.3 < player.getLocation().getX() && block.getLocation().getZ() - 0.3 < player.getLocation().getZ() && block.getLocation().getZ() + 0.3 < player.getLocation().getZ();
    }

    @EventHandler
    public void Interaction(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action action = e.getAction();
        if(action == Action.RIGHT_CLICK_BLOCK) {
            if(IsBlockSign(e.getClickedBlock())) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                if(sign.getLine(0).trim().equals("[엘리베이터]")) {
                    Location ploc = p.getLocation(), loc = sign.getLocation();
                    int x = loc.getBlockX(), z = loc.getBlockZ(), high = loc.getBlockY();
                    World w = p.getWorld();
                    if(p.isSneaking()) {
                        int down = Down(x, high, z, w);
                        if(down == -1) {
                            p.sendMessage(ChatColor.RED + "아래로 가는 층이 없습니다.");
                            return;
                        }

                        if(!IsStandEmpty(ploc.add(0, -down - 1, 0).getBlock(), p)) {
//                            p.sendMessage(ChatColor.RED + "이 곳에서 엘리베이터를 이용할 수 없습니다!");
//                            return;
                        }
                        p.teleport(ploc.add(0, 1, 0));
                        w.spawnParticle(PARTICLE_TYPE, ploc.add(0, 0.3, 0), PARTICLE);
                    } else {
                        int up = Up(x, high, z, w);
                        if(up == -1) {
                            p.sendMessage(ChatColor.RED + "위로 가는 층이 없습니다.");
                            return;
                        }

                        if(!IsStandEmpty(ploc.add(0, up - 1, 0).getBlock(), p)) {
//                            p.sendMessage(ChatColor.RED + "이 곳에서 엘리베이터를 이용할 수 없습니다!");
//                            return;
                        }
                        p.teleport(ploc.add(0, 1, 0));
                        w.spawnParticle(PARTICLE_TYPE, ploc.add(0, 0.3, 0), PARTICLE);
                    }
                }
            }
        }
    }
}
// TODO 고쳐야할 것 : 위로 가는거, 아래로 가는거 이펙트로 꾸미기
