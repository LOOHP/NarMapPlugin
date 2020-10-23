package com.loohp.nar.Story;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.inventory.ItemStack;

import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Utils.NBTUtils;

public class SpecialItems {
	
	public static final String BUBBLE_TEA = "bubble_tea";
	public static final String SK_SWORD = "sk_sword";
	public static final String MEMORY_CLOCK = "memory_clock";
	
	public static final String CHALLENGE_BIGGY = "challenge_biggy";
	public static final String CHALLENGE_LOOHP = "challenge_loohp";
	public static final String CHALLENGE_HENRY = "challenge_henry";
	public static final String CHALLENGE_CARRR = "challenge_carrr";
	
	public static final String LOOHP_LETTER = "loohp_letter";
	
	public static final String MINEHOE = "minesweeper_hoe";
	public static final String MINEWON = "minesweeper_won";
	public static final String MINELOST = "minesweeper_lost";
	
	public static final String CLOUDLAND = "cloudland";
	
	public final NarMapPlugin plugin;
	public final Map<String, ItemStack> map = new HashMap<>();
	public final Set<ItemStack> unplaceable = new HashSet<>();
	public final Set<ItemStack> challengeItems = new HashSet<>();
	
	public SpecialItems(NarMapPlugin plugin) {
		this.plugin = plugin;
		
		//ItemStack placeholder = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:stone\", tag: {display: {Name: '{\"extra\":[{\"text\":\"PLACEHOLDER\"}],\"text\":\"\"}'}}, Count: 1b}"));
		
		ItemStack bubble_tea = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:milk_bucket\", tag: {display: {Name: '{\"italic\":false,\"color\":\"#FFFFCC\",\"text\":\"珍珠奶茶\"}', Lore: ['{\"italic\":false,\"text\":\"(•◡•✿)\"}']}}, Count: 1b}"));
		map.put(BUBBLE_TEA, bubble_tea);
		
		ItemStack sk_sword = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:diamond_sword\", tag: {RepairCost: 3, HideFlags: 2, Enchantments: [{id: \"minecraft:fire_aspect\", lvl: 2s}, {id: \"minecraft:knockback\", lvl: 2s}, {id: \"minecraft:looting\", lvl: 3s}, {id: \"minecraft:mending\", lvl: 1s}, {id: \"minecraft:sharpness\", lvl: 5s}, {id: \"minecraft:unbreaking\", lvl: 5s}], Damage: 0, AttributeModifiers: [{Name: \"generic.movementSpeed\", Amount: 0.1d, Operation: 2, UUID: [I; 0, 77525, 0, 142945], Slot: \"mainhand\", AttributeName: \"minecraft:generic.movement_speed\"}, {Name: \"generic.knockbackResistance\", Amount: 0.05d, Operation: 2, UUID: [I; 0, 20090, 0, 116039], Slot: \"mainhand\", AttributeName: \"minecraft:generic.knockback_resistance\"}, {Name: \"generic.maxHealth\", Amount: 0.4d, Operation: 2, UUID: [I; 0, 47903, 0, 139932], Slot: \"mainhand\", AttributeName: \"minecraft:generic.max_health\"}, {Name: \"generic.attackDamage\", Amount: 17.5d, Operation: 0, UUID: [I; -1862269392, 2131772716, -1711375261, -1568086967], Slot: \"mainhand\", AttributeName: \"minecraft:generic.attack_damage\"}], display: {Name: '{\"text\":\"§d§lSunKnights §6§lSword\"}', Lore: ['{\"text\":\"§eThe Legendary Sword from §6the sky~~\"}', '{\"text\":\"§bThe sword gets better as\"}', '{\"text\":\"§bit recieves more §asharpness§b!\"}', '{\"text\":\"\"}', '{\"text\":\"§e+20 Attack Damage (Sharpness)\"}', '{\"text\":\"§c+40% Max Health\"}', '{\"text\":\"§b+10% Speed\"}', '{\"text\":\"§a+5% Knockback Resistance\"}', '{\"text\":\"\"}', '{\"text\":\"§aTotal Strikes: 171023\"}', '{\"text\":\"\"}']}}, Count: 1b}"));
		map.put(SK_SWORD, sk_sword);
		
		ItemStack memory_clock = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:clock\", tag: {display: {Name: '{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"yellow\",\"text\":\"記憶時鐘\"}],\"text\":\"\"}', Lore: ['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"light_purple\",\"text\":\"記載着一個人的記憶和過去\"}],\"text\":\"\"}']}, Enchantments: [{id: \"minecraft:mending\", lvl: 1s}]}, Count: 1b}"));
		map.put(MEMORY_CLOCK, memory_clock);
		unplaceable.add(memory_clock);
		
		ItemStack challenge_biggy = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:seagrass\", tag: {display: {Name: '{\"extra\":[{\"bold\":true,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"green\",\"text\":\"水草\"}],\"text\":\"\"}', Lore: ['{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"gray\",\"text\":\"腦力-10\"}],\"text\":\"\"}', '{\"extra\":[{\"text\":\"切勿胡亂服用\"}],\"text\":\"\"}', '{\"extra\":[{\"text\":\"聽說吃了後記憶力會衰退\"}],\"text\":\"\"}']}, Enchantments: [{id: \"minecraft:binding_curse\", lvl: 1s}], HideFlags:1}, Count: 1b}"));
		map.put(CHALLENGE_BIGGY, challenge_biggy);
		unplaceable.add(challenge_biggy);
		challengeItems.add(challenge_biggy);
		
		ItemStack challenge_loohp = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:leather_boots\", tag: {Damage: 0, display: {Name: '{\"extra\":[{\"italic\":false,\"color\":\"white\",\"text\":\"雪白的鞋子\"}],\"text\":\"\"}', color: 16383998, Lore: ['{\"extra\":[{\"text\":\"你的那一對穿了這麼久\"}],\"text\":\"\"}', '{\"extra\":[{\"text\":\"是時候換個新的！\"}],\"text\":\"\"}', '{\"extra\":[{\"text\":\"就當這對鞋子代替我走來找你\"}],\"text\":\"\"}']}, Enchantments: [{id: \"minecraft:protection\", lvl: 10s}]}, Count: 1b}"));
		map.put(CHALLENGE_LOOHP, challenge_loohp);
		challengeItems.add(challenge_loohp);
		
		ItemStack challenge_henry = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:painting\", tag: {display: {Name: '[{\"text\":\"Nar 2020.png\",\"italic\":false}]', Lore: ['[{\"text\":\"看起來很適合當桌布呢！\",\"italic\":false}]']}}, Count: 1b}"));
		map.put(CHALLENGE_HENRY, challenge_henry);
		challengeItems.add(challenge_henry);
		
		ItemStack challenge_carrr = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:wither_rose\", tag: {display: {Name: '{\"italic\":false,\"text\":\"來自愛人的祝福✿\"}', Lore: ['{\"italic\":false,\"text\":\"❁永恆的愛 永不凋零❁\"}']}}, Count: 1b}"));
		map.put(CHALLENGE_CARRR, challenge_carrr);
		challengeItems.add(challenge_carrr);
		
		ItemStack loohp_letter = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:written_book\", tag: {pages: ['{\"color\":\"blue\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://files.loohpjames.com/narmap/mail.html\"},\"text\":\"點擊查看\"}'], resolved: 1b, display: {Name: '{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"aqua\",\"text\":\"LOOHP的來信\"}],\"text\":\"\"}'}, author: \"LOOHP\", title: \"LOOHP的來信\"}, Count: 1b}"));
		map.put(LOOHP_LETTER, loohp_letter);
		
		ItemStack minesweeper_won = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:magenta_dye\", tag: {HideFlags: 1, display: {Name: '{\"italic\":false,\"color\":\"red\",\"text\":\"踩地雷專家\"}', Lore: ['{\"italic\":false,\"text\":\"成就物品\"}']}, Enchantments: [{id: \"minecraft:aqua_affinity\", lvl: 1s}]}, Count: 1b}"));
		map.put(MINEWON, minesweeper_won);
		
		ItemStack minesweeper_lost = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:orange_dye\", tag: {display: {Name: '{\"italic\":false,\"color\":\"gray\",\"text\":\"地雷\"}', Lore: ['{\"italic\":false,\"text\":\"你踩到我了。･ﾟ･(つд`ﾟ)･ﾟ･\"}']}}, Count: 1b}"));
		map.put(MINELOST, minesweeper_lost);
		
		ItemStack minesweeper_hoe = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:wooden_hoe\", tag: {display:{Name:'[{\"text\":\"地雷探測器\",\"color\":\"red\"}]'},Unbreakable:1,HideFlags:4,CanDestroy:[light_gray_carpet,blue_carpet,red_carpet]}, Count: 1b}"));
		map.put(MINEHOE, minesweeper_hoe);
		
		ItemStack cloudland = NBTUtils.getItemFromTag(NBTUtils.getNBTCompound("{id: \"minecraft:player_head\", tag: {SkullOwner: {Properties: {textures: [{Value: \"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE2ODI2YmRjN2IxMWVjNzUyMTczOTUxY2Q2ZjI4NGQ5OTYxYmNhZjY3NGU3MTgyOTZhNzhkYzM3ODMxYzQifX19\"}]}, Id: [I; -1955085314, 1261713167, -1209675978, 1039026979]}}, Count: 1b}"));
		map.put(CLOUDLAND, cloudland);
	}
	
	public ItemStack get(String key) {
		ItemStack item = map.get(key);
		return item == null ? null : item.clone();
	}
	
	public boolean isUnplaceable(ItemStack itemstack) {
		return unplaceable.stream().anyMatch(each -> each.isSimilar(itemstack));
	}
	
	public boolean isChallengeItem(ItemStack itemstack) {
		return challengeItems.stream().anyMatch(each -> each.isSimilar(itemstack));
	}

}
